package pojobase.oracle;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fss.cdrbin.thread.CDRBinManagerTnt;
import com.fss.dictionary.Dictionary;
import com.fss.dictionary.DictionaryNode;
import com.fss.thread.ManageableThread;
import com.fss.thread.ThreadConstant;
import com.fss.thread.ThreadManager;

import pojo.AppParam;
import pojo.GmapMarker;
import pojo.HoaHongThuCuoc;
import pojo.HoaHongThuCuocTrongDs;
import pojo.Mb6Fillter;
import pojo.Mb6Program;
import pojo.MsaleReseller;
import pojo.SubReg;
import pojobase.interfaces.MsaleBase;
import springweb.controllers.AjaxController;
import tool.Util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * 
 * @author Nguyễn Thành Thủy
 * 
 */

public class MsaleOracleBase extends OracleBase implements MsaleBase {
	private static HashMap<String, Mb6Fillter> filterBuiledMap = new HashMap<String, Mb6Fillter>();
	private ArrayList<String> provinceNumberList = new ArrayList<String>();
	public File template_dir, temp_dir = new File("download");
	public static File download_dir = new File("download");
	CDRBinManagerTnt tntService;

	@PostConstruct
	public void init() throws Exception {
		if (!download_dir.exists())
			download_dir.mkdirs();
		if (!temp_dir.exists())
			temp_dir.mkdirs();

		// tntService = new CDRBinManagerTnt();
		// tntService.start();

		// com.fss.cdrbin.thread.CDRBinManagerTnt.main(null);
	}

	@PreDestroy
	public void cleanUp() throws Exception {
		// tntService.stop();
	}

	// private static Logger logger =
	// Logger.getLogger(OracleBase.class.getName());

	@Autowired
	public MsaleOracleBase(org.apache.tomcat.jdbc.pool.DataSource dataSource) {
		super(dataSource);
		buildProvinceNumberList();
		// TODO Auto-generated constructor stub
	}

	public static String replaceNull(String inputStr) {
		try {
			return inputStr.replaceAll("null", "");
		} catch (Exception e) {
			return "";
		}
	}

	/*
	 * filter_type 0 - None fill 1 - Provine code fill 2- Province number fill
	 * 3- Province msale code fill
	 */
	private List<Map<String, String>> getData(ResultSet rs, int filter_type, String user_name, String app)
			throws SQLException {
		ResultSetMetaData rsMetaData = rs.getMetaData();
		int numberOfColumns = rsMetaData.getColumnCount();
		Map<String, String> pojo;
		pojo = new TreeMap<String, String>();
		List<Map<String, String>> pojoList = new LinkedList<Map<String, String>>();
		// put header
		pojo.put("000", "<th>STT");
		for (int i = 1; i <= numberOfColumns; i++) {
			pojo.put(String.format("%03d", i), "<th>" + rsMetaData.getColumnName(i));

		}
		pojoList.add(pojo);
		int rowcount = 0;

		if (filter_type == 1) {
			if (filterBuiledMap.get(user_name + ".." + app) == null)
				buildFilter(user_name, app);
			Mb6Fillter mb6Fillter = filterBuiledMap.get(user_name + ".." + app);
			while (rs != null && rs.next()) {
				if (mb6Fillter.isAllProvince() || rs.getString("province") == null
						|| rs.getString("province").equals("")
						|| mb6Fillter.getProvinceList().contains("" + rs.getString("province"))
								&& (mb6Fillter.isAllDistrict() || rs.getString("district") == null
										|| rs.getString("district").equals("")
										|| mb6Fillter.getDistrictList().contains("" + rs.getString("district")))) {
					rowcount = rowcount + 1;
					pojo = new TreeMap<String, String>();
					pojo.put("000", "<td>" + rowcount);
					for (int i = 1; i <= numberOfColumns; i++) {
						pojo.put(String.format("%03d", i), "<td>" + replaceNull(rs.getString(i)));
					}
					pojoList.add(pojo);

				}
			}
		} else {
			while (rs != null && rs.next()) {
				rowcount = rowcount + 1;
				pojo = new TreeMap<String, String>();
				pojo.put("000", "<td>" + rowcount);
				for (int i = 1; i <= numberOfColumns; i++) {
					pojo.put(String.format("%03d", i), "<td>" + replaceNull(rs.getString(i)));
				}
				pojoList.add(pojo);

			}
		}
		syslog("so ban tin:" + rowcount);
		return pojoList;
	}

	/*
	 * filter_type 0 - None fill 1 - Provine code fill 2- Province number fill
	 * 3- Province msale code fill
	 */
	private List<Map<String, String>> getData2(ResultSet rs, int filter_type, String user_name, String app)
			throws SQLException {
		ResultSetMetaData rsMetaData = rs.getMetaData();
		int numberOfColumns = rsMetaData.getColumnCount();
		Map<String, String> pojo;
		pojo = new TreeMap<String, String>();
		List<Map<String, String>> pojoList = new LinkedList<Map<String, String>>();
		// put header
		pojo.put("000", "<th>STT");
		for (int i = 1; i <= numberOfColumns; i++) {
			pojo.put(String.format("%03d", i), "<th>" + rsMetaData.getColumnName(i));
		}
		pojoList.add(pojo);
		int rowcount = 0;

		if (filter_type == 1) {
			String province = null, district = null;
			if (filterBuiledMap.get(user_name + ".." + app) == null)
				buildFilter(user_name, app);
			Mb6Fillter mb6Fillter = filterBuiledMap.get(user_name + ".." + app);
			while (rs != null && rs.next()) {
				try {
					province = rs.getString("province");
				} catch (Exception e) {
					province = null;
				}
				try {
					district = rs.getString("district");
				} catch (Exception e) {
					district = null;
				}
				if (mb6Fillter.isAllProvince() || province == null || province.equals("") || mb6Fillter
						.getProvinceList().contains(province.substring(province.length() - 3))
						&& (mb6Fillter.isAllDistrict() || district == null || district.equals("")
								|| mb6Fillter.getDistrictList().contains(district.substring(district.length() - 3)))) {
					rowcount = rowcount + 1;
					pojo = new TreeMap<String, String>();
					pojo.put("000", "<td>" + rowcount);
					for (int i = 1; i <= numberOfColumns; i++) {
						pojo.put(String.format("%03d", i), replaceNull(rs.getString(i)));
					}
					pojoList.add(pojo);

				}
			}
		} else {
			while (rs != null && rs.next()) {
				rowcount = rowcount + 1;
				pojo = new TreeMap<String, String>();
				pojo.put("000", "<td>" + rowcount);
				for (int i = 1; i <= numberOfColumns; i++) {
					pojo.put(String.format("%03d", i), replaceNull(rs.getString(i)));
				}
				pojoList.add(pojo);

			}
		}

		return pojoList;
	}

	@Override
	public List<MsaleReseller> getReselers(String user_name) throws SQLException {
		Connection conn = getConnection();
		Statement sttm = conn.createStatement();
		String sql = "SELECT /*+DRIVING_SITE(c)*/  SUBSTR(E.DISTRICT_CODE,1,3) PROVINCE, SUBSTR(E.DISTRICT_CODE,4) DISTRICT, "
				+ "TO_CHAR(C.MEMBER_ID) ID_DIEM_BAN, C.MEMBER_CODE MA_DIEM_BAN, C.MEMBER_NAME TEN_DIEM_BAN,"
				+ "C.CONTACT_PHONE, C.LATITUDE||',' ||C.LONGTITUDE googeladdress,replace(C.ADDRESS,chr(10),'') ADDRESS, C.WEBSITE MA_KENH,"
				+ "A.MEMBER_ID ID_NV_QL, A.MEMBER_CODE MA_NV_QL, A.MEMBER_NAME TEN_NV_QL"
				+ ",case when c.create_date >= trunc(sysdate-10,'MM') then 1 else 0 end new_member"
				+ "    FROM MSALES.MEMBER@MSALES_NEW C"
				+ " LEFT JOIN MSALES.SALE_ROUTE@MSALES_NEW B ON B.MEMBER_ID = C.MEMBER_ID AND B.STATUS = 1"
				+ " LEFT JOIN MSALES.MEMBER@MSALES_NEW A ON B.STAFF_ID = A.MEMBER_ID"
				+ " LEFT JOIN MSALES.DISTRICT@MSALES_NEW E ON C.PROVINCE_ID = E.PROVINCE_ID AND C.DISTRICT_ID = E.DISTRICT_ID"
				+ "  LEFT JOIN out_data.KPI_C6_CHANNEL K ON C.WEBSITE = K.CHANNEL_CODE"
				+ "    WHERE C.CHANNEL_PATH LIKE '>1>7>%' AND SUBSTR(E.DISTRICT_CODE,1,3) IN ('THO','NAN','HTI','QBI') AND C.MEMBER_TYPE !=7 AND C.STATUS  = 1"
				+ " and c.longtitude is not null and c.latitude is not null";
		ResultSet rs = sttm.executeQuery(sql);
		/*
		 * ResultSet rs = sttm.executeQuery(
		 * "SELECT tinh_thanh_pho id_tinh,quan_huyen id_huyen,id,ten_cua_hang,dia_chi,so_ez_nhan_tien,vi_do ||',' || kinh_do googeladdress"
		 * +
		 * "  FROM out_data.diem_ban_hang_msale_v where tinh_thanh_pho in (370,380,390,520)"
		 * +
		 * "  and nvl(so_ez_nhan_tien,'0')<>'0' and is_active=1 and vi_do is not null and kinh_do is not null"
		 * );
		 */
		List<MsaleReseller> pojoList = new LinkedList<MsaleReseller>();
		String app = "2";
		MsaleReseller pojo;
		if (filterBuiledMap.get(user_name + ".." + app) == null)
			buildFilter(user_name, app);
		Mb6Fillter mb6Fillter = filterBuiledMap.get(user_name + ".." + app);

		while (rs != null && rs.next()) {
			if (mb6Fillter.isAllProvince() || mb6Fillter.getProvinceList().contains("" + rs.getString("province"))
					&& (mb6Fillter.isAllDistrict()
							|| mb6Fillter.getDistrictList().contains("" + rs.getString("district")))) {
				pojo = new MsaleReseller();
				pojo.setId(rs.getString("ID_DIEM_BAN"));
				pojo.setTen_cua_hang(rs.getString("TEN_DIEM_BAN"));
				pojo.setDia_chi(rs.getString("ADDRESS"));
				pojo.setSo_ez_nhan_tien(rs.getString("CONTACT_PHONE"));
				pojo.setGoogelAddress(rs.getString("googeladdress"));
				pojo.setNew_member(rs.getString("new_member"));
				pojoList.add(pojo);
			}
		}
		// TODO Auto-generated method stub
		conn.close();
		return pojoList;
	}

	@Override
	public List<Mb6Program> getMb6Programs(int Type) throws SQLException {
		// TODO Auto-generated method stub
		Connection conn = getConnection();
		PreparedStatement sttm = conn.prepareStatement(
				"SELECT id, pro_type, pro_name, sta_date, end_date FROM mbf6_program WHERE pro_type = ?");
		sttm.setInt(1, Type);
		ResultSet rs = sttm.executeQuery();
		List<Mb6Program> pojoList = new LinkedList<Mb6Program>();
		Mb6Program pojo;
		while (rs != null && rs.next()) {
			pojo = new Mb6Program();
			pojo.setId(rs.getInt("id"));
			pojo.setPro_type(Type);
			pojo.setPro_name(rs.getString("pro_name"));
			pojo.setSta_date(rs.getDate("sta_date"));
			pojo.setEnd_date(rs.getDate("end_date"));
			pojoList.add(pojo);
		}
		// TODO Auto-generated method stub
		conn.close();
		return pojoList;
	}

	private void buildProvinceNumberList() {
		try {
			provinceNumberList = new ArrayList<String>();
			Connection conn = getConnection();
			PreparedStatement prpStm = conn
					.prepareStatement("select distinct province_number from address where center_from_2015=" + ct);
			ResultSet rs = prpStm.executeQuery();

			while (rs != null && rs.next()) {
				provinceNumberList.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	private void buildFilter(String user_name, String app) throws SQLException {
		Mb6Fillter mb6Fillter = new Mb6Fillter();

		// private boolean filterBuiled = false;

		Connection conn = getConnection();
		PreparedStatement prpStm = conn.prepareStatement(
				"SELECT role,province,province_number,province_msale,district,district_number,district_msale"
						+ "  FROM account_action_v where name=? and (app=? or role = 'DEVELOPER')");
		prpStm.setString(1, user_name);
		prpStm.setString(2, app);
		ResultSet rs = prpStm.executeQuery();
		String role, province, province_number, province_msale, district, district_number, district_msale;

		while (rs != null && rs.next()) {
			role = "" + rs.getString("role");
			province = "" + rs.getString("province");
			district = "" + rs.getString("district");
			province_number = "" + rs.getString("province_number");
			district_number = "" + rs.getString("district_number");
			province_msale = "" + rs.getString("province_msale");
			district_msale = "" + rs.getString("district_msale");
			mb6Fillter.getRoleList().add(role);
			if (!mb6Fillter.isAllProvince())
				if (province.equals("ALL"))
					mb6Fillter.setAllProvince(true);
				else {
					if (!province.equals("null")) {
						mb6Fillter.getProvinceList().add(province);
						mb6Fillter.getProvince_numberList().add(province_number);
						mb6Fillter.getProvince_msaleList().add(province_msale);
					}
					if (!mb6Fillter.isAllDistrict())
						if (district.equals("ALL"))
							mb6Fillter.setAllDistrict(true);
						else if (!district_msale.equals("null")) {
							mb6Fillter.getDistrictList().add(district);
							mb6Fillter.getDistrict_numberList().add(district_number);
							mb6Fillter.getDistrict_msaleList().add(district_msale);
						}
				}
		}
		if (!mb6Fillter.isAllProvince()
				&& (mb6Fillter.getProvinceList().size() > 1 || mb6Fillter.getDistrictList().isEmpty()))
			mb6Fillter.setAllDistrict(true);
		conn.close();
		filterBuiledMap.put(user_name + ".." + app, mb6Fillter);
	}

	@Override
	public List<SubReg> regSubsToProgram(int pro_id, int pro_type, String username, String[] preSubArray,
			String[] posSubArray, String month) throws SQLException {
		Connection conn = getConnection();
		conn.setAutoCommit(false);

		PreparedStatement prpStm = conn.prepareStatement("INSERT INTO sub_act_temp(isdn, active_time,is_gold, step) "
				+ "  VALUES (?, TO_DATE(?,'YYYY-MM-DD'),?, 0)");
		// syslog("BEGIN BUILD TMP");
		syslog("BEGIN BUILD TMP");
		for (int i = 0; i < preSubArray.length; i++) {
			prpStm.setString(1, preSubArray[i]);
			prpStm.setString(2, month + "-01");
			prpStm.setInt(3, 0);
			prpStm.addBatch();
		}
		for (int i = 0; i < posSubArray.length; i++) {
			prpStm.setString(1, posSubArray[i]);
			prpStm.setString(2, month + "-01");
			prpStm.setInt(3, 1);
			prpStm.addBatch();
		}
		prpStm.executeBatch();
		syslog("end BUILD TMP");
		prpStm = conn.prepareStatement("BEGIN mb6app.pre_assign_sub_reg; END;");
		prpStm.execute();
		syslog("end mb6app.pre_assign_sub_reg;");
		prpStm = conn.prepareStatement("BEGIN mb6app.assign_sub_reg(?,?,?,?); END;");
		prpStm.setInt(1, pro_id);
		prpStm.setInt(2, pro_type);
		prpStm.setString(3, username);
		prpStm.setInt(4, 1);
		prpStm.execute();
		prpStm = conn.prepareStatement(
				"SELECT isdn,sub_id,active_time,delete_date,is_gold,pro_id,account FROM sub_act_temp");
		ResultSet rs = prpStm.executeQuery();
		List<SubReg> pojoList = new LinkedList<SubReg>();
		SubReg pojo;
		while (rs != null && rs.next()) {
			pojo = new SubReg();
			pojo.setIsdn(rs.getString("isdn"));
			pojo.setActiveDate(rs.getDate("active_time"));
			pojo.setDeleteDate(rs.getDate("delete_date"));
			pojo.setSubId(rs.getLong("sub_id"));
			pojo.setGold(rs.getInt("is_gold") == 1);
			pojo.setPro_id(rs.getInt("pro_id"));
			pojo.setAccount(rs.getString("account"));
			pojoList.add(pojo);
		}
		conn.commit();

		// syslog("end");
		conn.close();
		return pojoList;

	}

	@Override
	public List<Map<String, String>> getTraThuong(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {

			JSONObject jsonObject = new JSONObject(json);
			String from_date = String.valueOf(jsonObject.get("from_date")),
					to_date = String.valueOf(jsonObject.get("to_date")), type = String.valueOf(jsonObject.get("type")),
					reg_by = String.valueOf(jsonObject.get("reg_by")), app = String.valueOf(jsonObject.get("app"));

			if (from_date == null || from_date.equals("")) {
				from_date = AjaxController.YYYY_MM_DD_FORMAT.format(new DateTime().plusDays(-1).toDate());
				to_date = AjaxController.YYYY_MM_DD_FORMAT.format(new DateTime().plusDays(-1).toDate());
			}
			if (reg_by == null || reg_by.equals("")) {
				reg_by = "ALL";
			}
			conn = getConnection();
			conn.setAutoCommit(false);

			List<Map<String, String>> pojoList = new LinkedList<Map<String, String>>();
			if (type.equals("0")) {// tra thuong kich hoat
				prpStm = conn.prepareStatement(
						"SELECT a.province, a.district, a.reg_by, b.ma_cua_hang, b.ten_cua_hang, a.dau_9, a.dau_1"
								+ " , a.tong, a.don_gia10, a.don_gia11, a.thanh_tien"
								+ " , a.kk FROM ( SELECT province, district, reg_by, mshop_id"
								+ " , kk, 10000 * kk don_gia10, 15000 * kk don_gia11, SUM(dau9) dau_9"
								+ " , SUM(dau1) dau_1, COUNT(sub_id) tong, SUM(dau9) * kk * 10000 + SUM(dau1) * kk * 15000"
								+ "  thanh_tien FROM (SELECT province, district, sub_type, reg_by, mshop_id, ma_cua_hang"
								+ " , ten_cua_hang, sub_id, isdn, CASE WHEN isdn LIKE '9%' THEN 1 ELSE 0 END dau9"
								+ " , CASE WHEN isdn LIKE '1%' THEN 1 ELSE 0 END dau1,CASE"
								+ "  WHEN reg_by IS NOT NULL AND mshop_id IS NOT NULL"
								+ "  AND sub_type NOT IN ('QTE', 'QSV', 'QDV')"
								+ "  THEN 1 ELSE 0 END kk,active_datetime, sim_serial"
								+ "  FROM cntt_c6.pre_vms_active WHERE cen_code = 6"
								+ "  AND(kit_id IS NULL OR kit_id <> 4) AND sub_type NOT IN ('ETK')"
								+ "  AND (reg_by=? or ?='ALL') AND active_datetime >="
								+ "  TO_DATE(?, 'YYYY-MM-DD') AND active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1)"
								+ "  GROUP BY province,district,reg_by,mshop_id,kk) a "
								+ "  LEFT JOIN out_data.diem_ban_hang_msale_v b ON a.mshop_id = b.id"
								+ "  ORDER BY reg_by, province");

				prpStm.setString(1, reg_by);
				prpStm.setString(2, reg_by);
				prpStm.setString(3, from_date);
				prpStm.setString(4, to_date);

				rs = prpStm.executeQuery();
				Map<String, String> pojo;
				// put header
				pojo = new TreeMap<String, String>();
				pojo.put("0province", "<td>MÃ TỈNH");
				pojo.put("1district", "<td>MÃ HUYỆN");
				pojo.put("2reg_by", "<td>EZ ĐĂNG KÝ");
				pojo.put("3ma_cua_hang", "<td>MÃ ĐIỂM BÁN");
				pojo.put("4ten_cua_hang", "<td>TÊN ĐIỂM BÁN");
				pojo.put("5dau_9", "<td>10 SỐ");
				pojo.put("6dau_1", "<td>11 SỐ");
				pojo.put("7tong", "<td>TỔNG");
				pojo.put("8don_gia10", "<td>ĐƠN GIÁ 10");
				pojo.put("9don_gia11", "<td>ĐƠN GIÁ 11");
				pojo.put("athanh_tien", "<td>THÀNH TIỀN");
				pojoList.add(pojo);

				if (filterBuiledMap.get(user_name + ".." + app) == null)
					buildFilter(user_name, app);
				Mb6Fillter mb6Fillter = filterBuiledMap.get(user_name + ".." + app);

				while (rs != null && rs.next()) {
					if (mb6Fillter.isAllProvince()
							|| mb6Fillter.getProvinceList().contains("" + rs.getString("province"))
									&& (mb6Fillter.isAllDistrict()
											|| mb6Fillter.getDistrictList().contains("" + rs.getString("district")))) {
						pojo = new TreeMap<String, String>();
						pojo.put("0province", replaceNull("<td>" + rs.getString("province")));
						pojo.put("1district", replaceNull("<td>" + rs.getString("district")));
						pojo.put("2reg_by", replaceNull("<td>" + rs.getString("reg_by")));
						pojo.put("3ma_cua_hang", replaceNull("<td>" + rs.getString("ma_cua_hang")));
						pojo.put("4ten_cua_hang", replaceNull("<td>" + rs.getString("ten_cua_hang")));
						pojo.put("5dau_9", replaceNull("<td>" + rs.getString("dau_9")));
						pojo.put("6dau_1", replaceNull("<td>" + rs.getString("dau_1")));
						pojo.put("7tong",
								replaceNull("<td class=\"canclick thc1\" ma_cua_hang=\"" + rs.getString("ma_cua_hang")
										+ "\"province=\"" + rs.getString("province") + "\"district=\""
										+ rs.getString("district") + "\"reg_by=\"" + rs.getString("reg_by") + "\"kk=\""
										+ rs.getString("kk") + "\">" + rs.getString("tong")));
						pojo.put("8don_gia10", replaceNull("<td>" + rs.getString("don_gia10")));
						pojo.put("9don_gia11", replaceNull("<td>" + rs.getString("don_gia11")));
						pojo.put("athanh_tien", replaceNull("<td>" + rs.getString("thanh_tien")));
						pojoList.add(pojo);
					}
				}
				pojo = new TreeMap<String, String>();
			}
			return pojoList;
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getTraThuongDetail(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String from_date = String.valueOf(jsonObject.get("from_date")),
					to_date = String.valueOf(jsonObject.get("to_date")), ez = String.valueOf(jsonObject.get("ez")),
					type = String.valueOf(jsonObject.get("type")), app = String.valueOf(jsonObject.get("app"));
			conn = getConnection();
			conn.setAutoCommit(false);
			if (ez == null || ez.equals(""))
				ez = "-1";
			if (type.equals("#tab1")) {
				prpStm = conn.prepareStatement("SELECT isdn, CASE WHEN reg_by IS NOT NULL AND mshop_id IS NOT NULL"
						+ "  AND sub_type NOT IN ('QTE', 'QSV', 'QDV') AND isdn LIKE '9%' THEN 10000"
						+ "  WHEN reg_by IS NOT NULL AND mshop_id IS NOT NULL"
						+ "  AND sub_type NOT IN ('QTE', 'QSV', 'QDV')"
						+ "  AND isdn LIKE '1%' THEN 15000 ELSE 0 END tien_kk"
						+ " , TO_CHAR(active_datetime, 'DD/MM/YYYY') active_datetime, sub_type"
						+ " , sim_serial, province, district, reg_by, mshop_id, ma_cua_hang, ten_cua_hang"
						+ "  FROM cntt_c6.pre_vms_active WHERE cen_code = 6"
						+ "  AND(kit_id IS NULL OR kit_id <> 4) AND sub_type NOT IN ('ETK')"
						+ "  AND active_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
						+ "  AND active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1"
						+ "  AND (reg_by = ? or '-1' = ?) order by province");

				prpStm.setString(1, from_date);
				prpStm.setString(2, to_date);
				prpStm.setString(3, ez);
				prpStm.setString(4, ez);

			} else if (type.equals("#tab2")) {
				prpStm = conn
						.prepareStatement(" SELECT /*+ index(b SCRATCH_DAILY_ID) index(a pre_vms_active_id2) */ a.isdn"
								+ " , TO_CHAR(active_datetime, 'DD/MM/YYYY') active_date, a.province"
								+ " , a.district, TO_CHAR(scratch_first_date, 'DD/MM/YYYY') scratch_first_date"
								+ " , a.scratch_first, CASE"
								+ "  WHEN so_ez_nhan_tien IS NOT NULL AND scratch_first >= 20000 AND scratch_first < 30000 THEN 10000"
								+ "  WHEN so_ez_nhan_tien IS NOT NULL AND scratch_first >= 30000 AND scratch_first < 50000 THEN 15000"
								+ "  WHEN so_ez_nhan_tien IS NOT NULL AND scratch_first >= 50000 AND scratch_first < 100000 THEN 30000"
								+ "  WHEN so_ez_nhan_tien IS NOT NULL AND scratch_first >= 100000 AND scratch_first < 200000 THEN 60000"
								+ "  WHEN so_ez_nhan_tien IS NOT NULL AND scratch_first >= 200000 AND scratch_first < 300000 THEN 90000"
								+ "  WHEN so_ez_nhan_tien IS NOT NULL AND scratch_first >= 300000 AND scratch_first < 500000 THEN 120000"
								+ "  WHEN so_ez_nhan_tien IS NOT NULL AND scratch_first >= 500000 THEN 150000"
								+ "  ELSE 0 END tien_kk, ma_cua_hang, ten_cua_hang"
								+ " , CASE WHEN so_ez_nhan_tien LIKE '84%' THEN so_ez_nhan_tien"
								+ "  WHEN so_ez_nhan_tien IS NOT NULL and TRANSLATE(so_ez_nhan_tien, ' +-.0123456789', ' ') IS NULL THEN '84' || TO_CHAR(TO_NUMBER(so_ez_nhan_tien))"
								+ "  ELSE so_ez_nhan_tien END so_ez_nhan_tien"
								+ "  FROM cntt_c6.pre_vms_active a INNER JOIN out_data.scratch_daily_v b"
								+ "  ON b.refill_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
								+ "  AND b.refill_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1"
								+ "  AND active_datetime >= TO_DATE('01/07/2015', 'dd/mm/yyyy')"
								+ "  AND b.mc_sub_id = a.sub_id AND a.cen_code = 6"
								+ "  WHERE (CASE WHEN so_ez_nhan_tien LIKE '84%' THEN so_ez_nhan_tien"
								+ "  WHEN so_ez_nhan_tien IS NOT NULL and TRANSLATE(so_ez_nhan_tien, ' +-.0123456789', ' ') IS NULL THEN '84' || TO_CHAR(TO_NUMBER(so_ez_nhan_tien))"
								+ "  ELSE so_ez_nhan_tien END = ? OR '-1' = ?)"
								+ "  AND TRUNC(a.scratch_first_date) = TRUNC(b.refill_datetime)"
								+ "  ORDER BY province, CASE WHEN so_ez_nhan_tien LIKE '84%' THEN so_ez_nhan_tien"
								+ "  WHEN so_ez_nhan_tien IS NOT NULL and TRANSLATE(so_ez_nhan_tien, ' +-.0123456789', ' ') IS NULL THEN '84' || TO_CHAR(TO_NUMBER(so_ez_nhan_tien))"
								+ "  ELSE so_ez_nhan_tien END");
				prpStm.setString(1, from_date);
				prpStm.setString(2, to_date);
				prpStm.setString(3, ez);
				prpStm.setString(4, ez);

			} else if (type.equals("#tab3")) {
				prpStm = conn.prepareStatement("SELECT isdn, active_date, province"
						+ " , district, ma_cua_hang, ten_cua_hang, SUM(psc) psc, SUM(psc_kk) psc_kk, CASE"
						+ "  WHEN so_ez_nhan_tien is not null and SUM(psc_kk) >= 150000 THEN 70000"
						+ "  WHEN so_ez_nhan_tien is not null and SUM(psc_kk) >= 110000 THEN 50000"
						+ "  WHEN so_ez_nhan_tien is not null and SUM(psc_kk) >= 60000 THEN 40000"
						+ "  WHEN so_ez_nhan_tien is not null and SUM(psc_kk) >= 25000 THEN 20000"
						+ "  WHEN so_ez_nhan_tien is not null and SUM(psc_kk) >= 20000 THEN 15000"
						+ "  WHEN so_ez_nhan_tien is not null and SUM(psc_kk) >= 15000 THEN 10000"
						+ "  WHEN so_ez_nhan_tien is not null and SUM(psc_kk) >= 10000 THEN 5000"
						+ "  ELSE 0 END tien_kk, TO_CHAR(MIN(issue_date), 'DD/MM/YYYY') min_psc_date"
						+ " , TO_CHAR(MAX(issue_date), 'DD/MM/YYYY') max_psc_date, so_ez_nhan_tien"
						+ "  FROM (SELECT a.isdn, TO_CHAR(a.active_datetime, 'DD/MM/YYYY') active_date"
						+ " , a.province, a.district, a.ma_cua_hang, a.ten_cua_hang, total_credit psc, CASE"
						+ "  WHEN b.item_id IN ('1', '5', '6', '7', '8', '14', '18', '19', '20', '21')"
						+ "  AND a.province ="
						+ "  DECODE(b.province_id, 31775, 'HTI', 31810, 'THO', 31794, 'QBI', 31772, 'NAN', NULL)"
						+ "  THEN b.total_credit ELSE 0 END"
						+ "  psc_kk, issue_date, CASE WHEN so_ez_nhan_tien LIKE '84%' THEN so_ez_nhan_tien"
						+ "  WHEN so_ez_nhan_tien IS NOT NULL AND TRANSLATE(so_ez_nhan_tien, ' +-.0123456789', ' ') IS NULL"
						+ "  THEN '84' || TO_CHAR(TO_NUMBER(so_ez_nhan_tien))"
						+ "  ELSE so_ez_nhan_tien END so_ez_nhan_tien FROM (SELECT *"
						+ "  FROM cntt_c6.pre_vms_active WHERE cen_code = 6"
						+ "  AND active_datetime >= TO_DATE(?, 'YYYY-MM-DD') - 90"
						+ "  AND active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1 - 90) a"
						+ "  INNER JOIN out_data.dttt_tbtt_isdn b ON a.sub_id = b.sub_id"
						+ "  AND b.issue_date >= TO_DATE(?, 'YYYY-MM-DD') - 90"
						+ "  AND b.issue_date < TO_DATE(?, 'YYYY-MM-DD') + 1"
						+ "  WHERE b.issue_date >= TRUNC(a.active_datetime) AND b.issue_date < TRUNC(a.active_datetime) + 90)"
						+ "  WHERE (so_ez_nhan_tien = ? OR '-1' = ?) GROUP BY isdn, active_date"
						+ " , province, district, ma_cua_hang, ten_cua_hang, so_ez_nhan_tien");
				prpStm.setString(1, from_date);
				prpStm.setString(2, to_date);
				prpStm.setString(3, from_date);
				prpStm.setString(4, to_date);
				prpStm.setString(5, ez);
				prpStm.setString(6, ez);

			}
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getDistrict(String user_name, String province) {
		// TODO Auto-generated method stub
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			List<Map<String, String>> pojoList = new LinkedList<Map<String, String>>();
			prpStm = conn.prepareStatement(
					"SELECT DISTINCT district_code, district FROM out_data.address WHERE province_code = ?");

			prpStm.setString(1, province);
			rs = prpStm.executeQuery();
			Map<String, String> pojo;
			pojo = new TreeMap<String, String>();
			while (rs != null && rs.next()) {
				pojo.put(rs.getString("district_code"), rs.getString("district"));
			}
			pojoList.add(pojo);
			return pojoList;
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getMsaleReseller(String user_name) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		String app = "2";
		try {
			buildFilter(user_name, app);
			conn = getConnection();
			Map<String, String> pojo;
			List<Map<String, String>> pojoList = new LinkedList<Map<String, String>>();

			prpStm = conn.prepareStatement("SELECT id_tinh,id_huyen,province, district,id_chanel,chanel"
					+ " , COUNT(id) so_diem_ban FROM (SELECT b.id id_tinh,c.id id_huyen"
					+ " , b.ten_tinh province,c.ten district, d.ez_deal,NVL(e.id, 0) id_chanel"
					+ " , NVL(e.name, 'Chua xác định') chanel,a.* FROM out_data.diem_ban_hang_msale_v a"
					+ "  INNER JOIN out_data.tinh_thanh_pho_msale_v b"
					+ "  ON a.tinh_thanh_pho = b.id AND a.tinh_thanh_pho IN"
					+ "  (370, 380, 390, 520) AND a.is_active = 1 INNER JOIN"
					+ "  out_data.quan_huyen_msale_v c ON a.quan_huyen = c.id LEFT JOIN"
					+ "  msale_resller d ON a.id = d.id LEFT JOIN sd6.sd5_sales_chanel e"
					+ "  ON d.chanel_id = e.id) GROUP BY id_tinh, id_huyen,province,district"
					+ " , id_chanel,chanel ORDER BY province, district");

			rs = prpStm.executeQuery();
			// put header
			pojo = new TreeMap<String, String>();
			pojo.put("0province", "<td>TỈNH");
			pojo.put("1district", "<td>HUYỆN");
			pojo.put("2chanel", "<td>KÊNH");
			pojo.put("3so_diem_ban", "<td>SỐ LƯỢNG");
			pojoList.add(pojo);
			if (filterBuiledMap.get(user_name + ".." + app) == null)
				buildFilter(user_name, app);
			Mb6Fillter mb6Fillter = filterBuiledMap.get(user_name + ".." + app);

			while (rs != null && rs.next()) {
				if (mb6Fillter.isAllProvince() || mb6Fillter.getProvince_msaleList()
						.contains("" + rs.getString("id_tinh"))
						&& (mb6Fillter.isAllDistrict()
								|| mb6Fillter.getDistrict_msaleList().contains("" + rs.getString("id_huyen")))) {
					pojo = new TreeMap<String, String>();
					pojo.put("0province", replaceNull("<td>" + rs.getString("province")));
					pojo.put("1district", replaceNull("<td>" + rs.getString("district")));
					pojo.put("2chanel", replaceNull("<td>" + rs.getString("chanel")));
					pojo.put("3so_diem_ban",
							replaceNull("<td class=\"canclick thc1\" align = \"right\" id_tinh = \""
									+ rs.getString("id_tinh") + "\" id_huyen = \"" + rs.getString("id_huyen")
									+ "\" id_chanel = \"" + rs.getString("id_chanel") + "\">"
									+ rs.getLong("so_diem_ban")));
					pojoList.add(pojo);
				}
			}
			// pojo = new TreeMap<String, String>();
			return pojoList;
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getMsaleResellerDetail(String user_name, String json) {
		// TODO Auto-generated method stub
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		JSONObject jsonObject = new JSONObject(json);
		String district = null, province = null, chanel = null, macuahang = null;
		try {
			macuahang = String.valueOf(jsonObject.get("macuahang"));
		} catch (Exception e) {
			macuahang = null;
			district = String.valueOf(jsonObject.get("district"));
			province = String.valueOf(jsonObject.get("province"));
			chanel = String.valueOf(jsonObject.get("chanel"));

		}
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			List<Map<String, String>> pojoList = new LinkedList<Map<String, String>>();

			if (macuahang == null) {
				prpStm = conn.prepareStatement(" select a.id,ma_cua_hang,ten_cua_hang,dia_chi,so_ez_nhan_tien,"
						+ "  b.EZ_DEAL,CHANEL_ID,c.name CHANEL"
						+ "  FROM out_data.diem_ban_hang_msale_v a left join MSALE_RESLLER b on a.id=b.id"
						+ "  left join sd6.sd5_sales_chanel c on b.CHANEL_ID=c.id"
						+ "  where a.tinh_thanh_pho IN (370, 380, 390, 520)"
						+ "  and (a.tinh_thanh_pho=? or ? =-1) and (a.quan_huyen = ? or ? =-1) and nvl(c.id,0)=?");
				syslog("district:" + district);
				if (province == null || province.equals(""))
					province = "-1";
				if (district == null || district.equals(""))
					district = "-1";
				syslog(district);

				prpStm.setString(1, province);
				prpStm.setString(2, province);
				prpStm.setString(3, district);
				prpStm.setString(4, district);
				prpStm.setString(5, chanel);
			} else {
				prpStm = conn.prepareStatement(" select a.id,ma_cua_hang,ten_cua_hang,dia_chi,so_ez_nhan_tien,"
						+ "  b.EZ_DEAL,CHANEL_ID,c.name CHANEL"
						+ "  FROM out_data.diem_ban_hang_msale_v a left join MSALE_RESLLER b on a.id=b.id"
						+ "  left join sd6.sd5_sales_chanel c on b.CHANEL_ID=c.id where ma_cua_hang=? ");
				prpStm.setString(1, macuahang);
			}
			rs = prpStm.executeQuery();
			Map<String, String> pojo;
			// put header
			pojo = new TreeMap<String, String>();
			pojo.put("0id", "<td>ID");
			pojo.put("1ma_cua_hang", "<td>MÃ CỬA HÀNG");
			pojo.put("2ten_cua_hang", "<td>TÊN CỬA HÀNG");
			pojo.put("3dia_chi", "<td>ĐỊA CHỈ");
			pojo.put("4so_ez_nhan_tien", "<td>SỐ EZ NHẬN TIỀN");
			pojo.put("5ez_deal", "<td>SỐ EZ THỎA THUẬN");
			pojo.put("6chanel", "<td>KÊNH");
			pojoList.add(pojo);
			while (rs != null && rs.next()) {
				pojo = new TreeMap<String, String>();
				pojo.put("0id", "<td class=\"id\">" + replaceNull(rs.getString("id")));
				pojo.put("1ma_cua_hang", replaceNull("<td>" + rs.getString("ma_cua_hang")));
				pojo.put("2ten_cua_hang", replaceNull("<td>" + rs.getString("ten_cua_hang")));
				pojo.put("3dia_chi", replaceNull("<td>" + rs.getString("dia_chi")));
				pojo.put("4so_ez_nhan_tien", replaceNull("<td>" + rs.getString("so_ez_nhan_tien")));
				pojo.put("5ez_deal", replaceNull("<td class=\"canclick thc2\" ez_deal=\"" + rs.getString("ez_deal")
						+ "\">" + rs.getString("ez_deal")));
				pojo.put("6chanel", replaceNull("<td class=\"canclick thc3\" chanel_id=\"" + rs.getString("CHANEL_ID")
						+ "\">" + rs.getString("chanel")));
				pojoList.add(pojo);
			}
			return pojoList;
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> updateAccount(String user_name, String json) {

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			// buildFilter(user_name,app);
			conn = getConnection();
			conn.setAutoCommit(false);

			JSONObject jsonObject = new JSONObject(json);

			String account = String.valueOf(jsonObject.get("account")), app = String.valueOf(jsonObject.get("app"));
			JSONArray roles = new JSONArray(jsonObject.get("roles").toString());
			JSONArray provices = new JSONArray(jsonObject.get("provinces").toString());
			JSONArray districts = new JSONArray(jsonObject.get("districts").toString());

			String xml_node = "";

			prpStm = conn.prepareStatement(
					"BEGIN INSERT INTO account(name) VALUES (lower(?)); EXCEPTION WHEN OTHERS THEN null; END;");
			prpStm.setString(1, account);
			prpStm.execute();
			prpStm.close();

			prpStm = conn.prepareStatement(
					"BEGIN update account set history_data = DELETEXML(history_data,'/types/type[@app=\"" + app
							+ "\"]') where name = ?; END;");
			prpStm.setString(1, account);
			prpStm.execute();
			prpStm.close();

			prpStm = conn.prepareStatement("BEGIN update account set history_data = "
					+ " CASE WHEN history_data IS NULL THEN xmltype('<types>' || ? || '</types>')"
					+ "  else APPENDCHILDXML(history_data, '/types', xmltype(?)) END where name = ?; END;");

			for (int i = 0; i < roles.length(); i++) {
				jsonObject = new JSONObject(roles.get(i).toString());
				xml_node = "<type r=\"" + String.valueOf(jsonObject.get("role")) + "\" app=\"" + app + "\"/>";

				prpStm.setString(1, xml_node);
				prpStm.setString(2, xml_node);
				prpStm.setString(3, account);
				prpStm.addBatch();
			}
			for (int i = 0; i < provices.length(); i++) {
				jsonObject = new JSONObject(provices.get(i).toString());
				xml_node = "<type p=\"" + String.valueOf(jsonObject.get("province_code")) + "\" pn=\""
						+ String.valueOf(jsonObject.get("province_number")) + "\" pm=\""
						+ String.valueOf(jsonObject.get("msale_province_id")) + "\" app=\"" + app + "\"/>";
				prpStm.setString(1, xml_node);
				prpStm.setString(2, xml_node);
				prpStm.setString(3, account);
				prpStm.addBatch();

			}
			for (int i = 0; i < districts.length(); i++) {
				jsonObject = new JSONObject(districts.get(i).toString());
				xml_node = "<type d=\"" + String.valueOf(jsonObject.get("district_code")) + "\" dn=\""
						+ String.valueOf(jsonObject.get("district_number")) + "\" dm=\""
						+ String.valueOf(jsonObject.get("msale_district_id")) + "\" app=\"" + app + "\"/>";

				prpStm.setString(1, xml_node);
				prpStm.setString(2, xml_node);
				prpStm.setString(3, account);
				prpStm.addBatch();

			}
			prpStm.executeBatch();
			prpStm.close();
			conn.commit();
			buildFilter(account, app);
			return null;
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}

	}

	@Override
	public List<Map<String, String>> updateMsaleResellerDetail(String user_name, String json) {
		// TODO Auto-generated method stub
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONArray jsonArray = new JSONArray(json);
			syslog(jsonArray.toString());

			JSONObject jsonObject;

			conn = getConnection();
			conn.setAutoCommit(false);

			String id = "0", ez_deal = "0", chanel = "0";
			String id_where = "a.id in (";
			for (int i = 0; i < jsonArray.length(); i++) {
				try {
					prpStm = conn.prepareStatement(
							"INSERT INTO MSALE_RESLLER(EZ_DEAL,CHANEL_ID,ID) VALUES (?, TO_number(?),?)");
					jsonObject = new JSONObject(jsonArray.get(i).toString());
					id = String.valueOf(jsonObject.get("id"));
					ez_deal = String.valueOf(jsonObject.get("ez_deal"));
					chanel = String.valueOf(jsonObject.get("chanel"));
					prpStm.setString(1, ez_deal);
					prpStm.setString(2, chanel);
					prpStm.setString(3, id);
					prpStm.execute();
				} catch (Exception e) {
					if (e.toString().startsWith("java.sql.SQLException: ORA-00001")) {
						prpStm = conn.prepareStatement("update MSALE_RESLLER set EZ_DEAL=?, CHANEL_ID=? where ID = ?");
						prpStm.setString(1, ez_deal);
						prpStm.setString(2, chanel);
						prpStm.setString(3, id);
						prpStm.execute();
					} // syslog(e.toString());

				}
				id_where = id_where + id + " ,";
			}
			id_where = id_where + " -1)";
			conn.commit();
			List<Map<String, String>> pojoList = new LinkedList<Map<String, String>>();
			prpStm = conn.prepareStatement(" select a.id,ma_cua_hang,ten_cua_hang,dia_chi,so_ez_nhan_tien,"
					+ "  b.EZ_DEAL,CHANEL_ID,c.name CHANEL"
					+ "  FROM out_data.diem_ban_hang_msale_v a left join MSALE_RESLLER b on a.id=b.id"
					+ "  left join sd6.sd5_sales_chanel c on b.CHANEL_ID=c.id where " + id_where);

			Map<String, String> pojo;
			// put header
			pojo = new TreeMap<String, String>();
			pojo.put("0id", "<td>ID");
			pojo.put("1ma_cua_hang", "<td>MÃ CỬA HÀNG");
			pojo.put("2ten_cua_hang", "<td>TÊN CỬA HÀNG");
			pojo.put("3dia_chi", "<td>ĐỊA CHỈ");
			pojo.put("4so_ez_nhan_tien", "<td>SỐ EZ NHẬN TIỀN");
			pojo.put("5ez_deal", "<td>SỐ EZ THỎA THUẬN");
			pojo.put("6chanel", "<td>KÊNH");
			pojoList.add(pojo);
			rs = prpStm.executeQuery();
			while (rs != null && rs.next()) {
				pojo = new TreeMap<String, String>();
				pojo.put("0id", "<td class=\"id\">" + replaceNull(rs.getString("id")));
				pojo.put("1ma_cua_hang", replaceNull("<td>" + rs.getString("ma_cua_hang")));
				pojo.put("2ten_cua_hang", replaceNull("<td>" + rs.getString("ten_cua_hang")));
				pojo.put("3dia_chi", replaceNull("<td>" + rs.getString("dia_chi")));
				pojo.put("4so_ez_nhan_tien", replaceNull("<td>" + rs.getString("so_ez_nhan_tien")));
				pojo.put("5ez_deal", replaceNull("<td class=\"canclick thc2\" ez_deal=\"" + rs.getString("ez_deal")
						+ "\">" + rs.getString("ez_deal")));
				pojo.put("6chanel", replaceNull("<td class=\"canclick thc3\" chanel_id=\"" + rs.getString("CHANEL_ID")
						+ "\">" + rs.getString("chanel")));
				pojoList.add(pojo);
			}
			return pojoList;
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getAccount(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {

			conn = getConnection();
			conn.setAutoCommit(false);
			JSONObject jsonObject = new JSONObject(json);
			String account = String.valueOf(jsonObject.get("account")), app = String.valueOf(jsonObject.get("app")),
					curtab = String.valueOf(jsonObject.get("curtab"));
			buildFilter(user_name, app);
			List<Map<String, String>> pojoList = new LinkedList<Map<String, String>>();

			if (curtab.equals("#tab1")) {
				prpStm = conn.prepareStatement("SELECT a.role asigned, b.name role, b.label"
						+ " ,b.app,CASE WHEN a.role IS NOT NULL THEN 'checked' ELSE NULL END role_checked"
						+ "  FROM account_action_v a RIGHT JOIN role b"
						+ "  ON a.role = b.name AND a.name = lower(?) WHERE b.app = ? AND EXISTS (SELECT 1"
						+ "  FROM account_action_v a INNER JOIN role b ON ( a.role = b.name"
						+ "  AND a.name = lower(?) AND((a.role LIKE '%ADMIN' AND b.app = ?) OR a.role = 'DEVELOPER')))");
				prpStm.setString(1, account);
				prpStm.setString(2, app);
				prpStm.setString(3, user_name);
				prpStm.setString(4, app);

				rs = prpStm.executeQuery();
				Map<String, String> pojo;
				// put header
				pojo = new TreeMap<String, String>();
				pojo.put("0role", "<th>");
				pojoList.add(pojo);
				// String check="false";

				if (filterBuiledMap.get(user_name + ".." + app) == null)
					buildFilter(user_name, app);
				Mb6Fillter mb6Fillter = filterBuiledMap.get(user_name + ".." + app);

				while (rs != null && rs.next()) {
					if (user_name.equals("thuy.nthanh") || mb6Fillter.getRoleList().contains("DEVELOPER")
							|| (!rs.getString("role").endsWith("ADMIN") && rs.getString("app").equals(app))) {
						pojo = new TreeMap<String, String>();
						pojo.put("0role",
								"<td><input type=\"checkbox\" id=\"rolecheck\" asigned=\"" + rs.getString("asigned")
										+ "\"role=\"" + rs.getString("role") + "\"" + rs.getString("role_checked")
										+ " >" + rs.getString("label"));
						pojoList.add(pojo);
					}
				}
			} else if (curtab.equals("#tab2")) {
				prpStm = conn.prepareStatement(" SELECT a.province code_asigned"
						+ " , CASE WHEN a.province IS NOT NULL THEN 'checked' ELSE NULL END pro_checked"
						+ " , c.province_code, c.province_number, c.msale_province_id"
						+ " , c.province province_label FROM account_action_v a RIGHT JOIN"
						+ "  (SELECT DISTINCT province_code, province_number,msale_province_id, province"
						+ "  FROM out_data.address WHERE province_code IN ('NAN', 'THO', 'HTI', 'QBI') union all"
						+ "  SELECT 'ALL' province_code, 0 province_number,0 msale_province_id, 'Tất cả' province"
						+ "  FROM dual) c ON a.province = c.province_code AND a.name = lower(?) and app=?");
				prpStm.setString(1, account);
				prpStm.setString(2, app);
				rs = prpStm.executeQuery();
				Map<String, String> pojo;
				// put header
				pojo = new TreeMap<String, String>();
				pojo.put("0", "<th>");
				pojoList.add(pojo);
				// String check="false";
				while (rs != null && rs.next()) {
					pojo = new TreeMap<String, String>();
					pojo.put("0", "<td><input type=\"checkbox\" id=\"pro_check\" code_asigned=\""
							+ rs.getString("code_asigned") + "\"province_code=\"" + rs.getString("province_code")
							+ "\"province_number=\"" + rs.getString("province_number") + "\"msale_province_id=\""
							+ rs.getString("msale_province_id") + "\"" + rs.getString("pro_checked") + " >"
							+ rs.getString("province_label"));
					pojoList.add(pojo);
				}
			} else {
				prpStm = conn.prepareStatement(" SELECT a.district district_code_asigned"
						+ " , CASE WHEN a.district IS NOT NULL THEN 'checked' ELSE NULL END"
						+ "  dis_checked, c.district_code, c.district_number"
						+ " , c.msale_district_id, c.district district_label"
						+ "  FROM account_action_v a RIGHT JOIN (SELECT DISTINCT district_code"
						+ " ,district_number,msale_district_id"
						+ " ,district FROM out_data.address WHERE province_code = ? UNION ALL"
						+ "  SELECT 'ALL' district_code, 0 district_number, 0 msale_district_id, 'Tất cả' district"
						+ "  FROM DUAL) c ON a.district = c.district_code AND a.name = lower(?) and app=? ORDER BY district_code ");

				String province_code = String.valueOf(jsonObject.get("province_code"));
				prpStm.setString(1, province_code);
				prpStm.setString(2, account);
				prpStm.setString(3, app);

				rs = prpStm.executeQuery();
				Map<String, String> pojo;
				// put header
				pojo = new TreeMap<String, String>();
				pojo.put("0", "<th>");
				pojoList.add(pojo);
				// String check="false";
				while (rs != null && rs.next()) {
					pojo = new TreeMap<String, String>();
					pojo.put("0", "<td><input type=\"checkbox\" id=\"dic_check\" district_code_asigned=\""
							+ rs.getString("district_code_asigned") + "\"district_code=\""
							+ rs.getString("district_code") + "\"district_number=\"" + rs.getString("district_number")
							+ "\"msale_district_id=\"" + rs.getString("msale_district_id") + "\""
							+ rs.getString("dis_checked") + " >" + rs.getString("district_label"));
					pojoList.add(pojo);
				}
			}
			return pojoList;
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getProgram(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String pro_type = String.valueOf(jsonObject.get("pro_type"));
			conn = getConnection();

			prpStm = conn.prepareStatement("SELECT id, pro_name, end_date "
					+ ",to_char(active_min,'dd/mm/yyyy') active_min, to_char(active_max,'dd/mm/yyyy') active_max"
					+ " FROM mbf6_program WHERE pro_type = ? order by pro_name desc ");

			prpStm.setString(1, pro_type);
			List<Map<String, String>> pojoList = new LinkedList<Map<String, String>>();
			rs = prpStm.executeQuery();
			Map<String, String> pojo;
			pojo = new TreeMap<String, String>();
			pojo.put("0",
					"<th><input type=\"checkbox\" id=\"KetThuc\" checked=\"true\" style=\"margin: 5px 0px 0px 5px;\">"
							+ " Chưa kết thúc");
			pojoList.add(pojo);
			while (rs != null && rs.next()) {
				pojo = new TreeMap<String, String>();
				pojo.put("0", "<td class=\"canclick\" id=\"" + rs.getString("id") + "\"end_date=\""
						+ replaceNull("" + rs.getString("end_date")) + "\"active_max=\""
						+ replaceNull("" + rs.getString("active_max")) + "\"active_min=\""
						+ replaceNull("" + rs.getString("active_min")) + "\">" + replaceNull(rs.getString("pro_name")));
				pojoList.add(pojo);
			}
			return pojoList;
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getProgramSub(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String type, from_date, to_date, danh_gia;
			String id = String.valueOf(jsonObject.get("id"));
			type = String.valueOf(jsonObject.get("type"));
			danh_gia = String.valueOf(jsonObject.get("danh_gia"));
			String app = String.valueOf(jsonObject.get("app"));
			try {
				from_date = String.valueOf(jsonObject.get("from_date")).replaceAll("-", "");
				to_date = String.valueOf(jsonObject.get("to_date")).replaceAll("-", "");

			} catch (Exception e1) {
				// id = "-1"; type = "-1";
				to_date = "";
				from_date = "";
			}
			if (to_date.equals(""))
				to_date = AjaxController.YYYYMMDD_FORMAT.format(new DateTime().plusDays(-2).toDate());
			conn = getConnection();
			List<Map<String, String>> pojoList = new LinkedList<Map<String, String>>();
			if (filterBuiledMap.get(user_name + ".." + app) == null)
				buildFilter(user_name, app);
			if (danh_gia.equals("0")) {
				prpStm = conn.prepareStatement(
						"select isdn,to_char(active_date,'YYYYMMDD') active_date ,account,prosub_code "
								+ "  from mbf6_program_sub_v where pro_id=? and is_gold = ? and isdn is not null "
								+ " order by prosub_code,account,active_date");
				prpStm.setString(1, id);
				prpStm.setString(2, type);
				rs = prpStm.executeQuery();
				pojoList = this.getData(rs, 0, user_name, app);

			} else {
				if (!type.equals("0")) {
					if (from_date.equals("")) {
						prpStm = conn
								.prepareStatement("BEGIN mb6app.buld_subscriber_tmp(to_date(?,'yyyyMMdd'),?); END;");
						prpStm.setString(1, to_date);
						prpStm.setString(2, id);
					} else {
						prpStm = conn.prepareStatement(
								"BEGIN mb6app.buld_subscriber_tmp(to_date(?,'yyyyMMdd'),to_date(?,'yyyyMMdd'),?); END;");
						prpStm.setString(1, from_date);
						prpStm.setString(2, to_date);
						prpStm.setString(3, id);
					}

					prpStm.execute();
					prpStm.close();
					prpStm = conn.prepareStatement("select * from mb6_subscriber_v order by isdn");

					rs = prpStm.executeQuery();
					pojoList = this.getData(rs, 1, user_name, app);
				} else {
					if (from_date.equals("")) {
						prpStm = conn.prepareStatement("BEGIN mb6app.analyze_program(?,to_date(?,'yyyyMMdd')); END;");
						prpStm.setString(1, id);
						prpStm.setString(2, to_date);
					} else {
						prpStm = conn.prepareStatement(
								"BEGIN mb6app.analyze_program(?,to_date(?,'yyyyMMdd'),to_date(?,'yyyyMMdd')); END;");
						prpStm.setString(1, id);
						prpStm.setString(2, from_date);
						prpStm.setString(3, to_date);
					}
					prpStm.execute();
					prpStm.close();
					prpStm = conn.prepareStatement("SELECT * from sub_analyze_tmp_v ORDER BY isdn");
					rs = prpStm.executeQuery();
					pojoList = getData(rs, 1, user_name, app);
				}
			}

			return pojoList;
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> programUpdateSub(String user_name, String json) {
		String separator = ";";
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String act_type = String.valueOf(jsonObject.get("act_type")),
					pro_type = String.valueOf(jsonObject.get("pro_type")),
					pro_id = String.valueOf(jsonObject.get("pro_id"))

					, app = String.valueOf(jsonObject.get("app")),
					aliving_date = String.valueOf(jsonObject.get("aliving_date")),
					pre_isdn_list = String.valueOf(jsonObject.get("pre_isdn_list")),
					pos_isdn_list = String.valueOf(jsonObject.get("pos_isdn_list")), prosub_code;

			try {
				prosub_code = String.valueOf(jsonObject.get("prosub_code"));
			} catch (Exception ex) {
				prosub_code = "";
			}
			pre_isdn_list = pre_isdn_list.replaceAll("\n", separator).replaceAll(",", separator).replaceAll(" ", "");
			pos_isdn_list = pos_isdn_list.replaceAll("\n", separator).replaceAll(",", separator).replaceAll(" ", "");
			String[] preSubArray = pre_isdn_list.split(separator);
			String[] posSubArray = pos_isdn_list.split(separator);

			conn = getConnection();
			conn.setAutoCommit(false);
			if (aliving_date == null || aliving_date.equals(""))
				aliving_date = "4000-01-01";

			prpStm = conn.prepareStatement("BEGIN INSERT INTO sub_act_temp(isdn,active_time,is_gold,step) "
					+ "VALUES (?,TO_DATE(?, 'YYYY-MM-DD'),?,0);EXCEPTION WHEN OTHERS THEN	NULL; END;");

			// syslog("BEGIN BUILD TMP");

			for (int i = 0; i < preSubArray.length; i++) {
				prpStm.setString(1, preSubArray[i]);
				prpStm.setString(2, aliving_date);
				prpStm.setInt(3, 0);
				prpStm.addBatch();
			}
			for (int i = 0; i < posSubArray.length; i++) {
				prpStm.setString(1, posSubArray[i]);
				prpStm.setString(2, aliving_date);
				prpStm.setInt(3, 1);
				prpStm.addBatch();
			}
			prpStm.executeBatch();
			prpStm.close();
			// syslog("end BUILD TMP");
			// prpStm = conn.prepareStatement("BEGIN mb6app.pre_assign_sub_reg3;
			// END;");
			// prpStm.execute();
			// syslog("end mb6app.pre_assign_sub_reg;");
			// prpStm.close();
			prpStm = conn.prepareStatement("BEGIN mb6app.pre_assign_sub_reg3; mb6app.assign_sub_reg(?,?,?,?,?); END;");
			prpStm.setString(1, pro_id);
			prpStm.setString(2, pro_type);
			prpStm.setString(3, user_name);
			prpStm.setString(4, act_type);
			prpStm.setString(5, prosub_code);
			prpStm.execute();
			prpStm.close();
			prpStm = conn.prepareStatement(
					"DECLARE v_active_min DATE; v_active_max DATE; BEGIN  SELECT MIN(active_date), MAX(active_date)"
							+ "  INTO v_active_min, v_active_max FROM mbf6_program_sub_v"
							+ "  WHERE pro_id = ?; UPDATE mbf6_program"
							+ "  SET active_min = v_active_min, active_max = v_active_max where id=?; END;");
			prpStm.setString(1, pro_id);
			prpStm.setString(2, pro_id);
			prpStm.execute();
			prpStm.close();
			prpStm = conn.prepareStatement(
					"SELECT a.*, b.pro_name FROM sub_act_temp a left join mbf6_program b on a.pro_id=b.id");
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> updateProgram(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String id = "", name = "", pro_type = "", delete = "0", confirmed = "0";
			id = String.valueOf(jsonObject.get("id"));
			pro_type = String.valueOf(jsonObject.get("pro_type"));
			try {
				delete = String.valueOf(jsonObject.get("delete"));
			} catch (Exception e) {
				confirmed = String.valueOf(jsonObject.get("confirmed"));
				syslog(confirmed);

				name = String.valueOf(jsonObject.get("name"));
				// end_date = String.valueOf(jsonObject.get("end_date"));
				// sta_date = String.valueOf(jsonObject.get("sta_date"));

			}
			conn = getConnection();

			if (delete.equals("1")) {
				prpStm = conn
						.prepareStatement("delete mbf6_program where id=? and EXTRACT(action_audit,'//sub') is null");
				prpStm.setString(1, id);
			} else if (id.equals("0")) {
				prpStm = conn.prepareStatement("insert into mbf6_program (id,pro_name,sta_date,pro_type)"
						+ "  values ((select max(id)+1 from mbf6_program ),?,sysdate,?)");

				prpStm.setString(1, name);
				prpStm.setString(2, pro_type);
			} else {
				prpStm = conn.prepareStatement(
						"BEGIN " + " update mbf6_program set pro_name =?, end_date = decode(?,1,sysdate,null) where id=? and end_date is null;"
								+ " END;");

				prpStm.setString(1, name);
				prpStm.setString(2, confirmed);
				prpStm.setString(3, id);
			}
			prpStm.execute();

			prpStm = conn.prepareStatement("SELECT id, pro_name, to_char(sta_date,'dd/mm/yyyy') sta_date"
					+ " , to_char(end_date,'dd/mm/yyyy') end_date FROM mbf6_program WHERE pro_type = ?");

			prpStm.setString(1, pro_type);
			List<Map<String, String>> pojoList = new LinkedList<Map<String, String>>();
			rs = prpStm.executeQuery();
			Map<String, String> pojo;
			pojo = new TreeMap<String, String>();
			pojo.put("0",
					"<td><input type=\"checkbox\" name=\"KetThuc\" checked=\"true\" style=\"margin: 5px 0px 0px 5px;\">"
							+ " Chưa kết thúc");
			pojoList.add(pojo);
			while (rs != null && rs.next()) {
				pojo = new TreeMap<String, String>();
				pojo.put("0",
						"<td class=\"canclick\" id=\"" + rs.getString("id") + "\"sta_date=\"" + rs.getString("sta_date")
								+ "\"end_date=\"" + replaceNull("" + rs.getString("end_date")) + "\">"
								+ replaceNull(rs.getString("pro_name")));
				pojoList.add(pojo);
			}
			conn.commit();
			return pojoList;

		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getTraThuongNapTheTh(String user_name, String json) {
		// TODO Auto-generated method stub
		JSONObject jsonObject = new JSONObject(json);
		String from_date = String.valueOf(jsonObject.get("from_date")),
				to_date = String.valueOf(jsonObject.get("to_date"));
		String app = String.valueOf(jsonObject.get("app"));
		// if (from_date == null || from_date.equals("")) {
		// from_date = AjaxController.YYYY_MM_DD_FORMAT.format(new DateTime()
		// .plusDays(-1).toDate());
		// to_date = AjaxController.YYYY_MM_DD_FORMAT.format(new DateTime()
		// .plusDays(-1).toDate());
		// }
		// if (ez == null || ez.equals("")) {
		// ez = "-1";
		// }

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			conn = getConnection();
			prpStm = conn.prepareStatement(" SELECT province, so_ez_nhan_tien, ma_cua_hang, ten_cua_hang"
					+ " , COUNT(CASE WHEN tien_kk > 0 THEN 1 ELSE NULL END) so_luong, SUM(tien_kk) tien_kk"
					+ "  FROM (SELECT /*+ index(b SCRATCH_DAILY_ID) index(a pre_vms_active_id2) */"
					+ "  a .isdn, TO_CHAR(active_datetime, 'DD/MM/YYYY') active_date, a.province, a.district"
					+ " , TO_CHAR(scratch_first_date, 'DD/MM/YYYY') scratch_first_date, a.scratch_first, CASE"
					+ "  WHEN so_ez_nhan_tien IS NOT NULL AND scratch_first >= 20000 AND scratch_first < 30000"
					+ "  THEN 10000"
					+ "  WHEN so_ez_nhan_tien IS NOT NULL AND scratch_first >= 30000 AND scratch_first < 50000"
					+ "  THEN 15000"
					+ "  WHEN so_ez_nhan_tien IS NOT NULL AND scratch_first >= 50000 AND scratch_first < 100000"
					+ "  THEN 30000"
					+ "  WHEN so_ez_nhan_tien IS NOT NULL AND scratch_first >= 100000 AND scratch_first < 200000"
					+ "  THEN 60000"
					+ "  WHEN so_ez_nhan_tien IS NOT NULL AND scratch_first >= 200000 AND scratch_first < 300000"
					+ "  THEN 90000"
					+ "  WHEN so_ez_nhan_tien IS NOT NULL AND scratch_first >= 300000 AND scratch_first < 500000"
					+ "  THEN 120000 WHEN so_ez_nhan_tien IS NOT NULL AND scratch_first >= 500000"
					+ "  THEN 150000 ELSE 0 END tien_kk, ma_cua_hang, ten_cua_hang, CASE"
					+ "  WHEN TO_CHAR(TO_NUMBER(so_ez_nhan_tien)) LIKE '84%' THEN TO_CHAR(TO_NUMBER(so_ez_nhan_tien))"
					+ "  WHEN so_ez_nhan_tien IS NOT NULL THEN '84' || TO_CHAR(TO_NUMBER(so_ez_nhan_tien))"
					+ "  ELSE so_ez_nhan_tien END so_ez_nhan_tien FROM cntt_c6.pre_vms_active a"
					+ "  INNER JOIN out_data.scratch_daily_v b ON b.refill_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
					+ "  AND b.refill_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1"
					+ "  AND active_datetime >= TO_DATE('01/07/2015', 'dd/mm/yyyy')"
					+ "  AND b.mc_sub_id = a.sub_id AND a.cen_code = 6"
					+ "  WHERE TRUNC(a.scratch_first_date) = TRUNC(b.refill_datetime))"
					+ "  GROUP BY province, so_ez_nhan_tien, ma_cua_hang, ten_cua_hang"
					+ "  HAVING SUM(tien_kk) > 0 ORDER BY province, so_ez_nhan_tien");

			prpStm.setString(1, from_date);
			prpStm.setString(2, to_date);
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}

	}

	@Override
	public List<Map<String, String>> getTraThuongKichHoatTh(String user_name, String json) {
		// TODO Auto-generated method stub
		JSONObject jsonObject = new JSONObject(json);
		String from_date = String.valueOf(jsonObject.get("from_date")),
				to_date = String.valueOf(jsonObject.get("to_date"));
		String app = String.valueOf(jsonObject.get("app"));
		// if (from_date == null || from_date.equals("")) {
		// from_date = AjaxController.YYYY_MM_DD_FORMAT.format(new DateTime()
		// .plusDays(-1).toDate());
		// to_date = AjaxController.YYYY_MM_DD_FORMAT.format(new DateTime()
		// .plusDays(-1).toDate());
		// }

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			conn = getConnection();
			prpStm = conn.prepareStatement(" SELECT a.province, a.reg_by"
					+ " , b.ma_cua_hang, b.ten_cua_hang, a.dau_9, a.dau_1, a.don_gia10, a.don_gia11, a.thanh_tien"
					+ "  FROM ( SELECT province, district, reg_by, mshop_id"
					+ " , kk, 10000 * kk don_gia10, 15000 * kk don_gia11, SUM(dau9) dau_9"
					+ " , SUM(dau1) dau_1, COUNT(sub_id) tong"
					+ " , SUM(dau9) * kk * 10000 + SUM(dau1) * kk * 15000 thanh_tien"
					+ "  FROM (SELECT province, district, sub_type, reg_by"
					+ " , mshop_id, ma_cua_hang, ten_cua_hang, sub_id, isdn"
					+ " , CASE WHEN isdn LIKE '9%' THEN 1 ELSE 0 END dau9"
					+ " , CASE WHEN isdn LIKE '1%' THEN 1 ELSE 0 END dau1, CASE"
					+ "  WHEN reg_by IS NOT NULL AND mshop_id IS NOT NULL"
					+ "  AND sub_type NOT IN ('QTE', 'QSV', 'QDV') THEN 1 ELSE 0 END kk"
					+ " , active_datetime, sim_serial FROM cntt_c6.pre_vms_active"
					+ "  WHERE cen_code = 6 AND(kit_id IS NULL OR kit_id <> 4) AND sub_type NOT IN ('ETK')"
					+ "  AND active_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
					+ "  AND active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1)"
					+ "  GROUP BY province, district, reg_by, mshop_id"
					+ " , kk) a LEFT JOIN out_data.diem_ban_hang_msale_v b"
					+ "  ON a.mshop_id = b.id WHERE kk = 1 ORDER BY province,reg_by");

			prpStm.setString(1, from_date);
			prpStm.setString(2, to_date);

			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}

	}

	@Override
	public List<Map<String, String>> getTraThuongPscTh(String user_name, String json) {
		JSONObject jsonObject = new JSONObject(json);
		String from_date = String.valueOf(jsonObject.get("from_date")),
				to_date = String.valueOf(jsonObject.get("to_date"));
		String app = String.valueOf(jsonObject.get("app"));

		// if (from_date == null || from_date.equals("")) {
		// from_date = AjaxController.YYYY_MM_DD_FORMAT.format(new DateTime()
		// .plusDays(-1).toDate());
		// to_date = AjaxController.YYYY_MM_DD_FORMAT.format(new DateTime()
		// .plusDays(-1).toDate());
		// }
		// if (ez == null || ez.equals("")) {
		// ez = "-1";
		// }

		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			conn = getConnection();
			prpStm = conn.prepareStatement("SELECT province, so_ez_nhan_tien, ma_cua_hang, ten_cua_hang"
					+ " , COUNT(isdn), SUM(tien_kk) FROM ( SELECT isdn, active_date, province, district"
					+ " , ma_cua_hang, ten_cua_hang, SUM(psc) psc, SUM(psc_kk) psc_kk, CASE"
					+ "  WHEN so_ez_nhan_tien is not null and SUM(psc_kk) >= 150000 THEN 70000"
					+ "  WHEN so_ez_nhan_tien is not null and SUM(psc_kk) >= 110000 THEN 50000"
					+ "  WHEN so_ez_nhan_tien is not null and SUM(psc_kk) >= 60000 THEN 40000"
					+ "  WHEN so_ez_nhan_tien is not null and SUM(psc_kk) >= 25000 THEN 20000"
					+ "  WHEN so_ez_nhan_tien is not null and SUM(psc_kk) >= 20000 THEN 15000"
					+ "  WHEN so_ez_nhan_tien is not null and SUM(psc_kk) >= 15000 THEN 10000"
					+ "  WHEN so_ez_nhan_tien is not null and SUM(psc_kk) >= 10000 THEN 5000"
					+ "  ELSE 0 END tien_kk, TO_CHAR(MIN(issue_date), 'DD/MM/YYYY') min_psc_date"
					+ " , TO_CHAR(MAX(issue_date), 'DD/MM/YYYY') max_psc_date, so_ez_nhan_tien FROM (SELECT a.isdn"
					+ " , TO_CHAR(a.active_datetime, 'DD/MM/YYYY') active_date, a.province, a.district"
					+ " , a.ma_cua_hang, a.ten_cua_hang, total_credit psc, CASE"
					+ "  WHEN b.item_id IN ('1', '5', '6', '7', '8', '14', '18', '19', '20', '21')"
					+ "  AND a.province = DECODE(b.province_id, 31775, 'HTI',"
					+ "  31810, 'THO', 31794, 'QBI', 31772, 'NAN', NULL) THEN b.total_credit ELSE 0"
					+ "  END psc_kk, issue_date, CASE WHEN so_ez_nhan_tien LIKE '84%' THEN so_ez_nhan_tien"
					+ "  WHEN so_ez_nhan_tien IS NOT NULL"
					+ "  AND TRANSLATE(so_ez_nhan_tien, ' +-.0123456789', ' ') IS NULL THEN"
					+ "  '84' || TO_CHAR(TO_NUMBER(so_ez_nhan_tien)) ELSE"
					+ "  so_ez_nhan_tien END so_ez_nhan_tien FROM (SELECT *"
					+ "  FROM cntt_c6.pre_vms_active WHERE cen_code = 6"
					+ "  AND active_datetime > TO_DATE(?, 'YYYY-MM-DD') - 90"
					+ "  AND active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1 - 90) a INNER JOIN"
					+ "  out_data.dttt_tbtt_isdn b ON a.sub_id = b.sub_id"
					+ "  AND b.issue_date >= TO_DATE(?, 'YYYY-MM-DD') - 90"
					+ "  AND b.issue_date < TO_DATE(?, 'YYYY-MM-DD') + 1"
					+ "  WHERE b.issue_date >= TRUNC(a.active_datetime) AND b.issue_date < TRUNC(a.active_datetime) + 90)"
					+ "  GROUP BY isdn, active_date, province, district"
					+ " , ma_cua_hang, ten_cua_hang, so_ez_nhan_tien)"
					+ "  GROUP BY province, ma_cua_hang, ten_cua_hang, so_ez_nhan_tien HAVING SUM(tien_kk) > 0");
			prpStm.setString(1, from_date);
			prpStm.setString(2, to_date);
			prpStm.setString(3, from_date);
			prpStm.setString(4, to_date);
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getTraThuongComboTh(String from_date, String to_date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, String>> CareHsrm(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String donvi = (String) jsonObject.get("donvi");
			String level = (String) jsonObject.get("level");
			String app = String.valueOf(jsonObject.get("app"));
			conn = getConnection();
			String sql;
			String sql_fix = ",SUM (NVL (hsrm_a, 0)) hsrm_a,SUM (NVL (hsrm_b, 0)) hsrm_b,"
					+ "   SUM (NVL (hsrm_c, 0)) hsrm_c,SUM (NVL (hsrm_d, 0)) hsrm_d," + "   ROUND ( 100"
					+ "       * (SUM (NVL (hsrm_a, 0)) - SUM (NVL (hsrm_b, 0)))"
					+ "       / DECODE (SUM (NVL (hsrm_a, 0)), 0, 1, SUM (NVL (hsrm_a, 0))),"
					+ "       2) hsrm_n, ROUND ( 100" + "       * (SUM (NVL (hsrm_c, 0)) - SUM (NVL (hsrm_d, 0)))"
					+ "       / DECODE (SUM (NVL (hsrm_c, 0)), 0, 1, SUM (NVL (hsrm_c, 0))),"
					+ "       2) hsrm_n1 FROM   out_data.hsrm_v";
			if (donvi.equals("666666")) {
				if (level.equals("0")) {
					sql = "  SELECT   TYPE" + sql_fix + " GROUP BY   TYPE order by type";
				} else if (level.equals("1")) {
					sql = "  SELECT   TYPE,province_code" + sql_fix + " GROUP BY   TYPE,province_code  order by type";

				} else {
					sql = "  SELECT   TYPE,province_code,district_code" + sql_fix
							+ " GROUP BY   TYPE,province_code,district_code   order by type, province_code";
				}
				prpStm = conn.prepareStatement(sql);

			} else if (provinceNumberList.contains(donvi)) {
				if (level.equals("0") || level.equals("1")) {
					sql = "  SELECT   TYPE,province_code" + sql_fix + " where province_code=get_province_code(?) "
							+ " GROUP BY   TYPE,province_code order by type ";

				} else {
					sql = "SELECT   TYPE,province_code,district_code" + sql_fix
							+ "  where province_code=get_province_code(?) "
							+ " GROUP BY   TYPE,province_code,district_code order by type, province_code";

				}
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, donvi);
			} else {
				sql = "SELECT   TYPE,province_code,district_code" + sql_fix
						+ " where get_district_number(province_code||district_code) = ? "
						+ " GROUP BY   TYPE,province_code,district_code";

				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, donvi);

			}
			syslog(sql);
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getNo3k3d(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			conn = getConnection();
			prpStm = conn.prepareStatement("SELECT * FROM hsrm_v WHERE vlr_3k3d is null and status =1");
			rs = prpStm.executeQuery();
			return getData(rs, 1, user_name, "3");
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getTinhTrangThueBao(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String from_date = String.valueOf(jsonObject.get("from_date")),
					to_date = String.valueOf(jsonObject.get("to_date")), type = String.valueOf(jsonObject.get("type")),
					donvi = String.valueOf(jsonObject.get("donvi")), level = String.valueOf(jsonObject.get("level"));
			String app = String.valueOf(jsonObject.get("app"));
			conn = getConnection();
			String sql = "";
			if (app.equals("1")) {
				if (type.equals("#tab1")) {// tab tra sau
					if (donvi.equals("666666")) {
						if (level.equals("0")) {
							sql = " SELECT '<td id=\"sub_type\">' || sub_type sub_type"
									+ ",'<td class=\"canclick cnumber\">' || COUNT(sub_id) tot_sub"
									+ " FROM subscriber_khdn_ptm_v WHERE sta_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
									+ "  AND sta_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1 GROUP BY ROLLUP(sub_type)";
						} else if (level.equals("1")) {
							sql = " SELECT '<td id=\"province\">' || province province"
									+ ",'<td id=\"sub_type\">' || sub_type sub_type"
									+ ",'<td class=\"canclick cnumber\">' || COUNT(sub_id) tot_sub"
									+ " FROM subscriber_khdn_ptm_v WHERE sta_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
									+ "  AND sta_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1"
									+ " GROUP BY ROLLUP(province, sub_type)";
						} else {
							sql = " SELECT '<td id=\"province\">' || province province"
									+ "      ,'<td id=\"district\">' || district district"
									+ "      ,'<td id=\"sub_type\">' || sub_type sub_type"
									+ "      ,'<td class=\"canclick cnumber\">' || COUNT(sub_id) tot_sub"
									+ " FROM subscriber_khdn_ptm_v WHERE sta_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
									+ "  AND sta_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1"
									+ " GROUP BY ROLLUP(province, district, sub_type)";
						}
						prpStm = conn.prepareStatement(sql);
						prpStm.setString(1, from_date);
						prpStm.setString(2, to_date);

					} else if (provinceNumberList.contains(donvi)) {
						if (level.equals("0") || level.equals("1")) {
							sql = " SELECT '<td id=\"province\">' || province province"
									+ "      ,'<td id=\"sub_type\">' || sub_type sub_type"
									+ "      ,'<td class=\"canclick cnumber\">' || COUNT(sub_id) tot_sub"
									+ " FROM subscriber_khdn_ptm_v WHERE sta_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
									+ "  AND sta_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1"
									+ "  AND province =get_province_code(?) GROUP BY ROLLUP(province, sub_type)";
						} else {
							sql = " SELECT '<td id=\"province\">' || province province"
									+ "      ,'<td id=\"district\">' || district district"
									+ "      ,'<td id=\"sub_type\">' || sub_type sub_type"
									+ "      ,'<td class=\"canclick cnumber\">' || COUNT(sub_id) tot_sub"
									+ " FROM subscriber_khdn_ptm_v WHERE sta_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
									+ "  AND sta_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1"
									+ "  AND province =get_province_code(?) "
									+ " GROUP BY ROLLUP(province, district, sub_type)";
						}
						prpStm = conn.prepareStatement(sql);
						prpStm.setString(1, from_date);
						prpStm.setString(2, to_date);
						prpStm.setString(3, donvi);
					} else {
						sql = " SELECT '<td id=\"province\">' || province province"
								+ "      ,'<td id=\"district\">' || district district"
								+ "      ,'<td id=\"sub_type\">' || sub_type sub_type"
								+ "      ,'<td class=\"canclick cnumber\">' || COUNT(sub_id) tot_sub"
								+ " FROM subscriber_khdn_ptm_v WHERE sta_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
								+ "  AND sta_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1"
								+ "  AND get_district_number(province||district) = ? "
								+ " GROUP BY ROLLUP(province, district, sub_type)";
						prpStm = conn.prepareStatement(sql);
						prpStm.setString(1, from_date);
						prpStm.setString(2, to_date);
						prpStm.setString(3, donvi);

					}
				} else {// tab tra truoc
					if (donvi.equals("666666")) {
						if (level.equals("0")) {
							sql = "  SELECT   '<td id=\"sub_type\">'|| sub_type sub_type"
									+ ",'<td class=\"canclick cnumber\">'|| COUNT(sub_id) tot_sub FROM   (SELECT	 sub_id"
									+ "					,hlr_isdn,active_datetime,province_reg,district_reg"
									+ "					,sub_type,emp_code_reg FROM	 out_data.mc_subscriber_mv"
									+ "			 WHERE		(delete_datetime IS NULL OR delete_datetime >= TO_DATE(?, 'YYYY-MM-DD'))"
									+ "					 AND cen_reg = 6 AND(kit_id = 4 OR sub_type = 'ETK')"
									+ "					 AND shop_code_reg IS NOT NULL"
									+ "					 AND active_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
									+ "					 AND active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1 )"
									+ " GROUP BY ROLLUP(sub_type)";
						} else if (level.equals("1")) {
							sql = "  SELECT   '<td id=\"province\">'|| province_reg province"
									+ ",'<td id=\"sub_type\">'|| sub_type sub_type"
									+ ",'<td class=\"canclick cnumber\">'|| COUNT(sub_id) tot_sub FROM   (SELECT	 sub_id"
									+ "					,hlr_isdn,active_datetime,province_reg,district_reg"
									+ "					,sub_type,emp_code_reg FROM	 out_data.mc_subscriber_mv"
									+ "			 WHERE		(delete_datetime IS NULL OR delete_datetime >= TO_DATE(?, 'YYYY-MM-DD'))"
									+ "					 AND cen_reg = 6 AND(kit_id = 4 OR sub_type = 'ETK')"
									+ "					 AND shop_code_reg IS NOT NULL"
									+ "					 AND active_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
									+ "					 AND active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1 )"
									+ " GROUP BY   ROLLUP(province_reg, sub_type)";
						} else {
							sql = "  SELECT   '<td id=\"province\">'|| province_reg province"
									+ ",'<td id=\"district\">'|| district_reg district"
									+ ",'<td id=\"sub_type\">'|| sub_type sub_type"
									+ ",'<td class=\"canclick cnumber\">'|| COUNT(sub_id) tot_sub FROM   (SELECT	 sub_id"
									+ "					,hlr_isdn,active_datetime,province_reg,district_reg"
									+ "					,sub_type,emp_code_reg FROM	 out_data.mc_subscriber_mv"
									+ "			 WHERE		(delete_datetime IS NULL OR delete_datetime >= TO_DATE(?, 'YYYY-MM-DD'))"
									+ "					 AND cen_reg = 6 AND(kit_id = 4 OR sub_type = 'ETK')"
									+ "					 AND shop_code_reg IS NOT NULL"
									+ "					 AND active_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
									+ "					 AND active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1 )"
									+ " GROUP BY   ROLLUP(province_reg,district_reg, sub_type)";
						}
						prpStm = conn.prepareStatement(sql);
						prpStm.setString(1, from_date);
						prpStm.setString(2, from_date);
						prpStm.setString(3, to_date);

					} else if (provinceNumberList.contains(donvi)) {
						if (level.equals("0") || level.equals("1")) {
							sql = "  SELECT   '<td id=\"province\">'|| province_reg province"
									+ ",'<td id=\"sub_type\">'|| sub_type sub_type"
									+ ",'<td class=\"canclick cnumber\">'|| COUNT(sub_id) tot_sub FROM   (SELECT	 sub_id"
									+ "					,hlr_isdn,active_datetime,province_reg,district_reg"
									+ "					,sub_type,emp_code_reg FROM	 out_data.mc_subscriber_mv"
									+ "			 WHERE		(delete_datetime IS NULL OR delete_datetime >= TO_DATE(?, 'YYYY-MM-DD'))"
									+ "					 AND cen_reg = 6 AND(kit_id = 4 OR sub_type = 'ETK')"
									+ "		   			 AND province_reg =get_province_code(?) "
									+ "					 AND shop_code_reg IS NOT NULL"
									+ "					 AND active_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
									+ "					 AND active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1 )"
									+ " GROUP BY   ROLLUP(province_reg, sub_type)";

						} else {
							sql = "  SELECT   '<td id=\"province\">'|| province_reg province"
									+ ",'<td id=\"district\">'|| district_reg district"
									+ ",'<td id=\"sub_type\">'|| sub_type sub_type"
									+ ",'<td class=\"canclick cnumber\">'|| COUNT(sub_id) tot_sub FROM   (SELECT	 sub_id"
									+ "					,hlr_isdn,active_datetime,province_reg,district_reg"
									+ "					,sub_type,emp_code_reg FROM	 out_data.mc_subscriber_mv"
									+ "			 WHERE		(delete_datetime IS NULL OR delete_datetime >= TO_DATE(?, 'YYYY-MM-DD'))"
									+ "					 AND cen_reg = 6 AND(kit_id = 4 OR sub_type = 'ETK')"
									+ "		   			 AND province_reg =get_province_code(?) "
									+ "					 AND shop_code_reg IS NOT NULL"
									+ "					 AND active_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
									+ "					 AND active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1 )"
									+ " GROUP BY   ROLLUP(province_reg,district_reg, sub_type)";
						}
						prpStm = conn.prepareStatement(sql);
						prpStm.setString(1, from_date);
						prpStm.setString(2, donvi);
						prpStm.setString(3, from_date);
						prpStm.setString(4, to_date);
					} else {
						sql = "  SELECT   '<td id=\"province\">'|| province_reg province"
								+ ",'<td id=\"district\">'|| district_reg district"
								+ ",'<td id=\"sub_type\">'|| sub_type sub_type"
								+ ",'<td class=\"canclick cnumber\">'|| COUNT(sub_id) tot_sub FROM   (SELECT	 sub_id"
								+ "					,hlr_isdn,active_datetime,province_reg,district_reg"
								+ "					,sub_type,emp_code_reg FROM	 out_data.mc_subscriber_mv"
								+ "			 WHERE		(delete_datetime IS NULL OR delete_datetime >= TO_DATE(?, 'YYYY-MM-DD'))"
								+ "					 AND cen_reg = 6 AND(kit_id = 4 OR sub_type = 'ETK')"
								+ "		     AND get_district_number(province_reg||district_reg) = ? "
								+ "					 AND shop_code_reg IS NOT NULL"
								+ "					 AND active_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
								+ "					 AND active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1 )"
								+ " GROUP BY   ROLLUP(province_reg,district_reg, sub_type)";
						prpStm = conn.prepareStatement(sql);
						prpStm.setString(1, from_date);
						prpStm.setString(2, donvi);
						prpStm.setString(3, from_date);
						prpStm.setString(4, to_date);
					}
				}
			} else {
				if (type.equals("#tab1")) {
					if (donvi.equals("666666")) {
						if (level.equals("0")) {
							sql = "  SELECT   '<td class=\"cnumber\">'||COUNT(sub_id) \"SỐ LƯỢNG THUÊ BAO\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"00\">'||COUNT(CASE WHEN status != '0' AND act_status = '00' THEN sub_id ELSE NULL END) \"HOẠT ĐỘNG 2 CHIỀU\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"01\">'||COUNT(CASE WHEN status != '0' AND act_status = '01' THEN sub_id ELSE NULL END) \"CHẶN 1C NỢ CƯỚC\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"02\">'||COUNT(CASE WHEN status != '0' AND act_status = '02' THEN sub_id ELSE NULL END) \"CHẶN 2C NỢ CƯỚC\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"10\">'||COUNT(CASE WHEN status != '0' AND act_status IN ('10', '11', '31', '30') THEN sub_id ELSE NULL END) \"CHẶN 1C KHÁC\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"20\">'||COUNT(CASE WHEN status != '0' AND act_status IN ('20', '21', '22', '12', '32') THEN sub_id ELSE NULL END) \"CHẶN 2C KHÁC\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 1) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 1 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 2) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 2 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 3) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 3 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 6) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 6 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 9) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 9 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 12) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 1 NĂM\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN status = '0' THEN sub_id ELSE NULL END) \"ĐÃ HỦY\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN status = '0' AND TRUNC(end_datetime, 'MM') = TRUNC(SYSDATE, 'MM') THEN sub_id ELSE NULL END) \"HỦY THÁNG HIỆN TẠI\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE"
									+ "					 WHEN status = '0' AND TRUNC(end_datetime, 'MM') = ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -1) THEN sub_id"
									+ "					 ELSE NULL END) \"HỦY THÁNG TRƯỚC\""
									+ "	FROM   out_data.subscriber_v WHERE cen_code = 6"
									+ "		   AND province IN ('THO', 'NAN', 'QBI', 'HTI')"
									+ "		   AND sta_datetime >= TO_DATE(?, 'YYYY-MM-DD') AND sta_datetime < TO_DATE(?, 'YYYY-MM-DD')  + 1";
						} else if (level.equals("1")) {
							sql = "  SELECT   '<td id=\"province\">'|| province province"
									+ "		  ,'<td class=\"cnumber\">'||COUNT(sub_id) \"SỐ LƯỢNG THUÊ BAO\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"00\">'||COUNT(CASE WHEN status != '0' AND act_status = '00' THEN sub_id ELSE NULL END) \"HOẠT ĐỘNG 2 CHIỀU\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"01\">'||COUNT(CASE WHEN status != '0' AND act_status = '01' THEN sub_id ELSE NULL END) \"CHẶN 1C NỢ CƯỚC\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"02\">'||COUNT(CASE WHEN status != '0' AND act_status = '02' THEN sub_id ELSE NULL END) \"CHẶN 2C NỢ CƯỚC\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"10\">'||COUNT(CASE WHEN status != '0' AND act_status IN ('10', '11', '31', '30') THEN sub_id ELSE NULL END) \"CHẶN 1C KHÁC\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"20\">'||COUNT(CASE WHEN status != '0' AND act_status IN ('20', '21', '22', '12', '32') THEN sub_id ELSE NULL END) \"CHẶN 2C KHÁC\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 1) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 1 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 2) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 2 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 3) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 3 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 6) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 6 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 9) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 9 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 12) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 1 NĂM\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN status = '0' THEN sub_id ELSE NULL END) \"ĐÃ HỦY\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN status = '0' AND TRUNC(end_datetime, 'MM') = TRUNC(SYSDATE, 'MM') THEN sub_id ELSE NULL END) \"HỦY THÁNG HIỆN TẠI\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE"
									+ "					 WHEN status = '0' AND TRUNC(end_datetime, 'MM') = ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -1) THEN sub_id"
									+ "					 ELSE NULL END) \"HỦY THÁNG TRƯỚC\""
									+ "	FROM   out_data.subscriber_v WHERE cen_code = 6"
									+ "		   AND province IN ('THO', 'NAN', 'QBI', 'HTI')"
									+ "		   AND sta_datetime >= TO_DATE(?, 'YYYY-MM-DD') AND sta_datetime < TO_DATE(?, 'YYYY-MM-DD')  + 1"
									+ " GROUP BY   province";

						} else {
							sql = "  SELECT   '<td id=\"province\">'|| province province,'<td id=\"district\">'|| district district"
									+ "		  ,'<td class=\"cnumber\">'||COUNT(sub_id) \"SỐ LƯỢNG THUÊ BAO\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"00\">'||COUNT(CASE WHEN status != '0' AND act_status = '00' THEN sub_id ELSE NULL END) \"HOẠT ĐỘNG 2 CHIỀU\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"01\">'||COUNT(CASE WHEN status != '0' AND act_status = '01' THEN sub_id ELSE NULL END) \"CHẶN 1C NỢ CƯỚC\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"02\">'||COUNT(CASE WHEN status != '0' AND act_status = '02' THEN sub_id ELSE NULL END) \"CHẶN 2C NỢ CƯỚC\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"10\">'||COUNT(CASE WHEN status != '0' AND act_status IN ('10', '11', '31', '30') THEN sub_id ELSE NULL END) \"CHẶN 1C KHÁC\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"20\">'||COUNT(CASE WHEN status != '0' AND act_status IN ('20', '21', '22', '12', '32') THEN sub_id ELSE NULL END) \"CHẶN 2C KHÁC\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 1) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 1 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 2) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 2 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 3) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 3 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 6) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 6 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 9) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 9 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 12) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 1 NĂM\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN status = '0' THEN sub_id ELSE NULL END) \"ĐÃ HỦY\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN status = '0' AND TRUNC(end_datetime, 'MM') = TRUNC(SYSDATE, 'MM') THEN sub_id ELSE NULL END) \"HỦY THÁNG HIỆN TẠI\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE"
									+ "					 WHEN status = '0' AND TRUNC(end_datetime, 'MM') = ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -1) THEN sub_id"
									+ "					 ELSE NULL END) \"HỦY THÁNG TRƯỚC\""
									+ "	FROM   out_data.subscriber_v WHERE cen_code = 6"
									+ "		   AND province IN ('THO', 'NAN', 'QBI', 'HTI')"
									+ "		   AND sta_datetime >= TO_DATE(?, 'YYYY-MM-DD') AND sta_datetime < TO_DATE(?, 'YYYY-MM-DD')  + 1"
									+ " GROUP BY   province,district";
						}
						prpStm = conn.prepareStatement(sql);
						prpStm.setString(1, from_date);
						prpStm.setString(2, to_date);

					} else if (provinceNumberList.contains(donvi)) {
						if (level.equals("0") || level.equals("1")) {
							sql = "  SELECT   '<td id=\"province\">'|| province province"
									+ "		  ,'<td class=\"cnumber\">'||COUNT(sub_id) \"SỐ LƯỢNG THUÊ BAO\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"00\">'||COUNT(CASE WHEN status != '0' AND act_status = '00' THEN sub_id ELSE NULL END) \"HOẠT ĐỘNG 2 CHIỀU\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"01\">'||COUNT(CASE WHEN status != '0' AND act_status = '01' THEN sub_id ELSE NULL END) \"CHẶN 1C NỢ CƯỚC\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"02\">'||COUNT(CASE WHEN status != '0' AND act_status = '02' THEN sub_id ELSE NULL END) \"CHẶN 2C NỢ CƯỚC\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"10\">'||COUNT(CASE WHEN status != '0' AND act_status IN ('10', '11', '31', '30') THEN sub_id ELSE NULL END) \"CHẶN 1C KHÁC\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"20\">'||COUNT(CASE WHEN status != '0' AND act_status IN ('20', '21', '22', '12', '32') THEN sub_id ELSE NULL END) \"CHẶN 2C KHÁC\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 1) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 1 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 2) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 2 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 3) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 3 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 6) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 6 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 9) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 9 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 12) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 1 NĂM\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN status = '0' THEN sub_id ELSE NULL END) \"ĐÃ HỦY\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN status = '0' AND TRUNC(end_datetime, 'MM') = TRUNC(SYSDATE, 'MM') THEN sub_id ELSE NULL END) \"HỦY THÁNG HIỆN TẠI\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE"
									+ "					 WHEN status = '0' AND TRUNC(end_datetime, 'MM') = ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -1) THEN sub_id"
									+ "					 ELSE NULL END) \"HỦY THÁNG TRƯỚC\""
									+ "	FROM   out_data.subscriber_v WHERE cen_code = 6"
									+ "		   AND province =get_province_code(?) "
									+ "		   AND sta_datetime >= TO_DATE(?, 'YYYY-MM-DD') AND sta_datetime < TO_DATE(?, 'YYYY-MM-DD')  + 1"
									+ " GROUP BY   province";

						} else {
							sql = "  SELECT   '<td id=\"province\">'|| province province,'<td id=\"district\">'|| district district"
									+ "		  ,'<td class=\"cnumber\">'||COUNT(sub_id) \"SỐ LƯỢNG THUÊ BAO\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"00\">'||COUNT(CASE WHEN status != '0' AND act_status = '00' THEN sub_id ELSE NULL END) \"HOẠT ĐỘNG 2 CHIỀU\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"01\">'||COUNT(CASE WHEN status != '0' AND act_status = '01' THEN sub_id ELSE NULL END) \"CHẶN 1C NỢ CƯỚC\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"02\">'||COUNT(CASE WHEN status != '0' AND act_status = '02' THEN sub_id ELSE NULL END) \"CHẶN 2C NỢ CƯỚC\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"10\">'||COUNT(CASE WHEN status != '0' AND act_status IN ('10', '11', '31', '30') THEN sub_id ELSE NULL END) \"CHẶN 1C KHÁC\""
									+ "		  ,'<td class=\"canclick cnumber\" act_status = \"20\">'||COUNT(CASE WHEN status != '0' AND act_status IN ('20', '21', '22', '12', '32') THEN sub_id ELSE NULL END) \"CHẶN 2C KHÁC\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 1) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 1 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 2) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 2 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 3) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 3 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 6) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 6 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 9) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 9 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 12) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 1 NĂM\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN status = '0' THEN sub_id ELSE NULL END) \"ĐÃ HỦY\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN status = '0' AND TRUNC(end_datetime, 'MM') = TRUNC(SYSDATE, 'MM') THEN sub_id ELSE NULL END) \"HỦY THÁNG HIỆN TẠI\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE"
									+ "					 WHEN status = '0' AND TRUNC(end_datetime, 'MM') = ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -1) THEN sub_id"
									+ "					 ELSE NULL END) \"HỦY THÁNG TRƯỚC\""
									+ "	FROM   out_data.subscriber_v WHERE cen_code = 6"
									+ "		   AND province =get_province_code(?) "
									+ "		   AND sta_datetime >= TO_DATE(?, 'YYYY-MM-DD') AND sta_datetime < TO_DATE(?, 'YYYY-MM-DD')  + 1"
									+ " GROUP BY   province,district";

						}
						prpStm = conn.prepareStatement(sql);
						prpStm.setString(1, donvi);
						prpStm.setString(2, from_date);
						prpStm.setString(3, to_date);
					} else {
						sql = "  SELECT   '<td id=\"province\">'|| province province,'<td id=\"district\">'|| district district"
								+ "		  ,'<td class=\"cnumber\">'||COUNT(sub_id) \"SỐ LƯỢNG THUÊ BAO\""
								+ "		  ,'<td class=\"canclick cnumber\" act_status = \"00\">'||COUNT(CASE WHEN status != '0' AND act_status = '00' THEN sub_id ELSE NULL END) \"HOẠT ĐỘNG 2 CHIỀU\""
								+ "		  ,'<td class=\"canclick cnumber\" act_status = \"01\">'||COUNT(CASE WHEN status != '0' AND act_status = '01' THEN sub_id ELSE NULL END) \"CHẶN 1C NỢ CƯỚC\""
								+ "		  ,'<td class=\"canclick cnumber\" act_status = \"02\">'||COUNT(CASE WHEN status != '0' AND act_status = '02' THEN sub_id ELSE NULL END) \"CHẶN 2C NỢ CƯỚC\""
								+ "		  ,'<td class=\"canclick cnumber\" act_status = \"10\">'||COUNT(CASE WHEN status != '0' AND act_status IN ('10', '11', '31', '30') THEN sub_id ELSE NULL END) \"CHẶN 1C KHÁC\""
								+ "		  ,'<td class=\"canclick cnumber\" act_status = \"20\">'||COUNT(CASE WHEN status != '0' AND act_status IN ('20', '21', '22', '12', '32') THEN sub_id ELSE NULL END) \"CHẶN 2C KHÁC\""
								+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 1) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 1 THÁNG\""
								+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 2) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 2 THÁNG\""
								+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 3) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 3 THÁNG\""
								+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 6) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 6 THÁNG\""
								+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 9) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 9 THÁNG\""
								+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN ADD_MONTHS(sta_datetime, 12) <= NVL(end_datetime, SYSDATE) THEN sub_id ELSE NULL END) \"ĐẠT 1 NĂM\""
								+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN status = '0' THEN sub_id ELSE NULL END) \"ĐÃ HỦY\""
								+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN status = '0' AND TRUNC(end_datetime, 'MM') = TRUNC(SYSDATE, 'MM') THEN sub_id ELSE NULL END) \"HỦY THÁNG HIỆN TẠI\""
								+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE"
								+ "					 WHEN status = '0' AND TRUNC(end_datetime, 'MM') = ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -1) THEN sub_id"
								+ "					 ELSE NULL END) \"HỦY THÁNG TRƯỚC\""
								+ "	FROM   out_data.subscriber_v WHERE cen_code = 6"
								+ "		   AND get_district_number(province||district) = ? "
								+ "		   AND sta_datetime >= TO_DATE(?, 'YYYY-MM-DD') AND sta_datetime < TO_DATE(?, 'YYYY-MM-DD')  + 1"
								+ " GROUP BY   province,district";

						prpStm = conn.prepareStatement(sql);
						prpStm.setString(1, donvi);
						prpStm.setString(2, from_date);
						prpStm.setString(3, to_date);

					}
				} else {// tab tra truoc
					if (donvi.equals("666666")) {
						if (level.equals("0")) {
							sql = "SELECT '<td class=\"cnumber\">' || COUNT(b.sub_id) \"SỐ LƯỢNG THUÊ BAO\""
									+ "		  ,'<td class=\"cnumber\" act_status = \"00\">'"
									+ "		   || COUNT(CASE WHEN b.hlr_status != '0' AND b.hlr_act_status = '00' THEN b.sub_id ELSE NULL END) "
									+ "			   \"HOẠT ĐỘNG 2 CHIỀU\""
									+ "		  ,'<td class=\"canclick cnumber\"  act_status = \"1\">'"
									+ "		   || COUNT(CASE WHEN b.hlr_status != '0'"
									+ "							 AND INSTR(b.hlr_act_status, '2') <= 0"
									+ "							 AND INSTR(b.hlr_act_status, '1') > 0"
									+ "						THEN b.sub_id ELSE NULL END) \"CHẶN 1 CHIỀU\""
									+ "		  ,'<td class=\"canclick cnumber\"  act_status = \"2\">'"
									+ "		   || COUNT(CASE WHEN b.hlr_status != '0' AND INSTR(b.hlr_act_status, '2') > 0 THEN b.sub_id ELSE NULL END) \"CHẶN 2 CHIỀU\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN stn>=2 THEN b.sub_id ELSE NULL END) \"NẠP HƠN 1 THẺ\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 1) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 1 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 2) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 2 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 3) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 3 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 6) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 6 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 9) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 9 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 12) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 1 NĂM\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE WHEN b.hlr_status = '0' THEN b.sub_id ELSE NULL END) \"ĐÃ HỦY\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN b.hlr_status = '0' AND TRUNC(b.delete_datetime, 'MM') = TRUNC(SYSDATE, 'MM') THEN b.sub_id"
									+ "						ELSE NULL END) \"HỦY THÁNG HIỆN TẠI\""
									+ ",'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN b.hlr_status = '0' AND TRUNC(b.delete_datetime, 'MM') = ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -1)"
									+ "						THEN b.sub_id ELSE NULL"
									+ "					END) \"HỦY THÁNG TRƯỚC\" FROM out_data.mc_subscriber_mv b"
									+ "		   where	  b.cen_reg = 6 and (delete_datetime IS NULL OR delete_datetime >= TO_DATE(?, 'YYYY-MM-DD'))"
									+ "			  AND b.active_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
									+ "			  AND b.active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1"
									+ " 	and b.province_reg IN ('THO', 'NAN', 'QBI', 'HTI')";

						} else if (level.equals("1")) {
							sql = "SELECT  		'<td id=\"province\">' || b.province_reg province"
									+ "		  ,'<td class=\"cnumber\">' || COUNT(b.sub_id) \"SỐ LƯỢNG THUÊ BAO\""
									+ "		  ,'<td class=\"cnumber\" act_status = \"00\">'"
									+ "		   || COUNT(CASE WHEN b.hlr_status != '0' AND b.hlr_act_status = '00' THEN b.sub_id ELSE NULL END)"
									+ "			   \"HOẠT ĐỘNG 2 CHIỀU\""
									+ "		  ,'<td class=\"canclick cnumber\"  act_status = \"1\">'"
									+ "		   || COUNT(CASE WHEN b.hlr_status != '0'"
									+ "							 AND INSTR(b.hlr_act_status, '2') <= 0"
									+ "							 AND INSTR(b.hlr_act_status, '1') > 0"
									+ "						THEN b.sub_id ELSE NULL END) \"CHẶN 1 CHIỀU\""
									+ "		  ,'<td class=\"canclick cnumber\"  act_status = \"2\">'"
									+ "		   || COUNT(CASE WHEN b.hlr_status != '0' AND INSTR(b.hlr_act_status, '2') > 0 THEN b.sub_id ELSE NULL END) \"CHẶN 2 CHIỀU\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN stn>=2 THEN b.sub_id ELSE NULL END) \"NẠP HƠN 1 THẺ\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 1) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 1 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 2) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 2 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 3) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 3 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 6) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 6 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 9) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 9 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 12) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 1 NĂM\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE WHEN b.hlr_status = '0' THEN b.sub_id ELSE NULL END) \"ĐÃ HỦY\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN b.hlr_status = '0' AND TRUNC(b.delete_datetime, 'MM') = TRUNC(SYSDATE, 'MM') THEN b.sub_id"
									+ "						ELSE NULL END) \"HỦY THÁNG HIỆN TẠI\""
									+ ",'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN b.hlr_status = '0' AND TRUNC(b.delete_datetime, 'MM') = ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -1)"
									+ "						THEN b.sub_id ELSE NULL"
									+ "					END) \"HỦY THÁNG TRƯỚC\" FROM out_data.mc_subscriber_mv b"
									+ "		   where	  b.cen_reg = 6 and (delete_datetime IS NULL OR delete_datetime >= TO_DATE(?, 'YYYY-MM-DD')) AND b.active_datetime >= TO_DATE(?, 'YYYY-MM-DD') "
									+ "			  AND b.active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1 and"
									+ " 	 b.province_reg IN ('THO', 'NAN', 'QBI', 'HTI') GROUP BY   b.province_reg";
						} else {
							sql = "SELECT 			 '<td id=\"province\">' || b.province_reg province,'<td id=\"district\">' || b.district_reg district"
									+ "		  ,'<td class=\"cnumber\">' || COUNT(b.sub_id) \"SỐ LƯỢNG THUÊ BAO\""
									+ "		  ,'<td class=\"cnumber\" act_status = \"00\">'"
									+ "		   || COUNT(CASE WHEN b.hlr_status != '0' AND b.hlr_act_status = '00' THEN b.sub_id ELSE NULL END)"
									+ "			   \"HOẠT ĐỘNG 2 CHIỀU\""
									+ "		  ,'<td class=\"canclick cnumber\"  act_status = \"1\">'"
									+ "		   || COUNT(CASE WHEN b.hlr_status != '0'"
									+ "							 AND INSTR(b.hlr_act_status, '2') <= 0"
									+ "							 AND INSTR(b.hlr_act_status, '1') > 0"
									+ "						THEN b.sub_id ELSE NULL END) \"CHẶN 1 CHIỀU\""
									+ "		  ,'<td class=\"canclick cnumber\"  act_status = \"2\">'"
									+ "		   || COUNT(CASE WHEN b.hlr_status != '0' AND INSTR(b.hlr_act_status, '2') > 0 THEN b.sub_id ELSE NULL END) \"CHẶN 2 CHIỀU\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN stn>=2 THEN b.sub_id ELSE NULL END) \"NẠP HƠN 1 THẺ\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 1) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 1 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 2) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 2 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 3) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 3 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 6) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 6 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 9) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 9 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 12) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 1 NĂM\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE WHEN b.hlr_status = '0' THEN b.sub_id ELSE NULL END) \"ĐÃ HỦY\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN b.hlr_status = '0' AND TRUNC(b.delete_datetime, 'MM') = TRUNC(SYSDATE, 'MM') THEN b.sub_id"
									+ "						ELSE NULL END) \"HỦY THÁNG HIỆN TẠI\""
									+ ",'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN b.hlr_status = '0' AND TRUNC(b.delete_datetime, 'MM') = ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -1)"
									+ "						THEN b.sub_id ELSE NULL"
									+ "					END) \"HỦY THÁNG TRƯỚC\" FROM	out_data.mc_subscriber_mv b"
									+ "		  where  b.cen_reg = 6 and (delete_datetime IS NULL OR delete_datetime >= TO_DATE(?, 'YYYY-MM-DD')) AND b.active_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
									+ "			  AND b.active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1"
									+ " 	and b.province_reg IN ('THO', 'NAN', 'QBI', 'HTI')"
									+ " GROUP BY   b.province_reg, b.district_reg";
						}
						prpStm = conn.prepareStatement(sql);
						prpStm.setString(1, from_date);
						prpStm.setString(2, from_date);
						prpStm.setString(3, to_date);

					} else if (provinceNumberList.contains(donvi)) {
						if (level.equals("0") || level.equals("1")) {
							sql = "SELECT 			  '<td class=\"cnumber\">' || COUNT(b.sub_id) \"SỐ LƯỢNG THUÊ BAO\""
									+ "		  ,'<td class=\"cnumber\" act_status = \"00\">'"
									+ "		   || COUNT(CASE WHEN b.hlr_status != '0' AND b.hlr_act_status = '00' THEN b.sub_id ELSE NULL END)"
									+ "			   \"HOẠT ĐỘNG 2 CHIỀU\""
									+ "		  ,'<td class=\"canclick cnumber\"  act_status = \"1\">'"
									+ "		   || COUNT(CASE WHEN b.hlr_status != '0'"
									+ "							 AND INSTR(b.hlr_act_status, '2') <= 0"
									+ "							 AND INSTR(b.hlr_act_status, '1') > 0"
									+ "						THEN b.sub_id ELSE NULL END) \"CHẶN 1 CHIỀU\""
									+ "		  ,'<td class=\"canclick cnumber\"  act_status = \"2\">'"
									+ "		   || COUNT(CASE WHEN b.hlr_status != '0' AND INSTR(b.hlr_act_status, '2') > 0 THEN b.sub_id ELSE NULL END) \"CHẶN 2 CHIỀU\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN stn>=2 THEN b.sub_id ELSE NULL END) \"NẠP HƠN 1 THẺ\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 1) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 1 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 2) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 2 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 3) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 3 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 6) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 6 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 9) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 9 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 12) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 1 NĂM\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE WHEN b.hlr_status = '0' THEN b.sub_id ELSE NULL END) \"ĐÃ HỦY\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN b.hlr_status = '0' AND TRUNC(b.delete_datetime, 'MM') = TRUNC(SYSDATE, 'MM') THEN b.sub_id"
									+ "						ELSE NULL END) \"HỦY THÁNG HIỆN TẠI\""
									+ ",'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN b.hlr_status = '0' AND TRUNC(b.delete_datetime, 'MM') = ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -1)"
									+ "						THEN b.sub_id ELSE NULL"
									+ "					END) \"HỦY THÁNG TRƯỚC\" FROM	out_data.mc_subscriber_mv b "
									+ "		   where  b.cen_reg = 6 and (delete_datetime IS NULL OR delete_datetime >= TO_DATE(?, 'YYYY-MM-DD')) AND b.active_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
									+ "			  AND b.active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1"
									+ "   and b.province_reg =get_province_code(?)";

						} else {
							sql = "SELECT  		'<td id=\"province\">' || b.province_reg province,'<td id=\"district\">' || b.district_reg district"
									+ "		  ,'<td class=\"cnumber\">' || COUNT(b.sub_id) \"SỐ LƯỢNG THUÊ BAO\""
									+ "		  ,'<td class=\"cnumber\" act_status = \"00\">'"
									+ "		   || COUNT(CASE WHEN b.hlr_status != '0' AND b.hlr_act_status = '00' THEN b.sub_id ELSE NULL END)"
									+ "			   \"HOẠT ĐỘNG 2 CHIỀU\""
									+ "		  ,'<td class=\"canclick cnumber\"  act_status = \"1\">'"
									+ "		   || COUNT(CASE WHEN b.hlr_status != '0'"
									+ "							 AND INSTR(b.hlr_act_status, '2') <= 0"
									+ "							 AND INSTR(b.hlr_act_status, '1') > 0"
									+ "						THEN b.sub_id ELSE NULL END) \"CHẶN 1 CHIỀU\""
									+ "		  ,'<td class=\"canclick cnumber\"  act_status = \"2\">'"
									+ "		   || COUNT(CASE WHEN b.hlr_status != '0' AND INSTR(b.hlr_act_status, '2') > 0 THEN b.sub_id ELSE NULL END) \"CHẶN 2 CHIỀU\""
									+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN stn>=2 THEN b.sub_id ELSE NULL END) \"NẠP HƠN 1 THẺ\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 1) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 1 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 2) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 2 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 3) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 3 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 6) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 6 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 9) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 9 THÁNG\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN ADD_MONTHS(b.active_datetime, 12) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
									+ "						ELSE NULL END) \"ĐẠT 1 NĂM\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE WHEN b.hlr_status = '0' THEN b.sub_id ELSE NULL END) \"ĐÃ HỦY\""
									+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN b.hlr_status = '0' AND TRUNC(b.delete_datetime, 'MM') = TRUNC(SYSDATE, 'MM') THEN b.sub_id"
									+ "						ELSE NULL END) \"HỦY THÁNG HIỆN TẠI\""
									+ ",'<td class=\"cnumber\">' || COUNT(CASE"
									+ "						WHEN b.hlr_status = '0' AND TRUNC(b.delete_datetime, 'MM') = ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -1)"
									+ "						THEN b.sub_id ELSE NULL"
									+ "					END) \"HỦY THÁNG TRƯỚC\" FROM out_data.mc_subscriber_mv b"
									+ "		   where	  b.cen_reg = 6 and (delete_datetime IS NULL OR delete_datetime >= TO_DATE(?, 'YYYY-MM-DD')) AND b.active_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
									+ "			  AND b.active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1"
									+ "   and   b.province_reg =get_province_code(?)  GROUP BY   b.province_reg, b.district_reg";

						}
						prpStm = conn.prepareStatement(sql);
						prpStm.setString(1, from_date);
						prpStm.setString(2, from_date);
						prpStm.setString(3, to_date);
						prpStm.setString(4, donvi);

					} else {
						sql = "SELECT  			  '<td>' || b.province_reg province,'<td>' || b.district_reg district"
								+ "		  ,'<td class=\"cnumber\">' || COUNT(b.sub_id) \"SỐ LƯỢNG THUÊ BAO\""
								+ "		  ,'<td class=\"cnumber\" act_status = \"00\">'"
								+ "		   || COUNT(CASE WHEN b.hlr_status != '0' AND b.hlr_act_status = '00' THEN b.sub_id ELSE NULL END)"
								+ "			   \"HOẠT ĐỘNG 2 CHIỀU\""
								+ "		  ,'<td class=\"canclick cnumber\"  act_status = \"1\">'"
								+ "		   || COUNT(CASE WHEN b.hlr_status != '0'"
								+ "							 AND INSTR(b.hlr_act_status, '2') <= 0"
								+ "							 AND INSTR(b.hlr_act_status, '1') > 0"
								+ "						THEN b.sub_id ELSE NULL END) \"CHẶN 1 CHIỀU\""
								+ "		  ,'<td class=\"canclick cnumber\"  act_status = \"2\">'"
								+ "		   || COUNT(CASE WHEN b.hlr_status != '0' AND INSTR(b.hlr_act_status, '2') > 0 THEN b.sub_id ELSE NULL END) \"CHẶN 2 CHIỀU\""
								+ "		  ,'<td class=\"cnumber\">'||COUNT(CASE WHEN stn>=2 THEN b.sub_id ELSE NULL END) \"NẠP HƠN 1 THẺ\""
								+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
								+ "						WHEN ADD_MONTHS(b.active_datetime, 1) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
								+ "						ELSE NULL END) \"ĐẠT 1 THÁNG\""
								+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
								+ "						WHEN ADD_MONTHS(b.active_datetime, 2) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
								+ "						ELSE NULL END) \"ĐẠT 2 THÁNG\""
								+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
								+ "						WHEN ADD_MONTHS(b.active_datetime, 3) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
								+ "						ELSE NULL END) \"ĐẠT 3 THÁNG\""
								+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
								+ "						WHEN ADD_MONTHS(b.active_datetime, 6) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
								+ "						ELSE NULL END) \"ĐẠT 6 THÁNG\""
								+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
								+ "						WHEN ADD_MONTHS(b.active_datetime, 9) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
								+ "						ELSE NULL END) \"ĐẠT 9 THÁNG\""
								+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
								+ "						WHEN ADD_MONTHS(b.active_datetime, 12) <= NVL(b.delete_datetime, SYSDATE) THEN b.sub_id"
								+ "						ELSE NULL END) \"ĐẠT 1 NĂM\""
								+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE WHEN b.hlr_status = '0' THEN b.sub_id ELSE NULL END) \"ĐÃ HỦY\""
								+ "		  ,'<td class=\"cnumber\">' || COUNT(CASE"
								+ "						WHEN b.hlr_status = '0' AND TRUNC(b.delete_datetime, 'MM') = TRUNC(SYSDATE, 'MM') THEN b.sub_id"
								+ "						ELSE NULL END) \"HỦY THÁNG HIỆN TẠI\""
								+ ",'<td class=\"cnumber\">' || COUNT(CASE"
								+ "						WHEN b.hlr_status = '0' AND TRUNC(b.delete_datetime, 'MM') = ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -1)"
								+ "						THEN b.sub_id ELSE NULL"
								+ "					END) \"HỦY THÁNG TRƯỚC\" FROM out_data.mc_subscriber_mv b"
								+ "		   where	  b.cen_reg = 6 and (delete_datetime IS NULL OR delete_datetime >= TO_DATE(?, 'YYYY-MM-DD')) AND b.active_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
								+ "			  AND b.active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1"
								+ "   and get_district_number(province_reg||district_reg) = ? "
								+ " GROUP BY   b.province_reg, b.district_reg";

						prpStm = conn.prepareStatement(sql);
						prpStm.setString(1, from_date);
						prpStm.setString(2, from_date);
						prpStm.setString(3, to_date);
						prpStm.setString(4, donvi);

					}
				}
			}
			syslog(sql);

			rs = prpStm.executeQuery();
			return getData2(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getKhdnNewActive(String user_name, Date from_date, Date to_date) {
		Connection conn = null;
		ResultSet rs = null;
		String app = "1";
		PreparedStatement prpStm = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			if (filterBuiledMap.get(user_name + ".." + app) == null)
				buildFilter(user_name, app);
			Mb6Fillter mb6Fillter = filterBuiledMap.get(user_name + ".." + app);
			String province_condition = "", district_condition = "";

			if (!mb6Fillter.isAllProvince()) {
				for (String province : mb6Fillter.getProvinceList()) {
					province_condition = province_condition + " ,'" + province + "'";
				}
				try {
					province_condition = " and province_reg in (" + province_condition.substring(2) + " )";
				} catch (Exception e) {
					province_condition = "";
				}
			}
			if (!mb6Fillter.isAllDistrict()) {
				for (String district : mb6Fillter.getDistrictList()) {
					district_condition = district_condition + " ,'" + district + "'";
				}
				try {
					district_condition = " and district_reg in (" + district_condition.substring(2) + " )";
				} catch (Exception e) {
					district_condition = "";
				}

			}
			String sql = "  SELECT   province_reg province, sub_type, COUNT(sub_id) tot_sub FROM   (SELECT	 sub_id"
					+ "					,hlr_isdn,active_datetime,province_reg,district_reg"
					+ "					,sub_type,emp_code_reg FROM	 out_data.mc_subscriber_mv"
					+ "			 WHERE		(delete_datetime IS NULL OR delete_datetime >= TO_DATE(?, 'YYYY-MM-DD'))"
					+ "					 AND cen_reg = 6 AND(kit_id = 4 OR sub_type = 'ETK')"
					+ "					 AND shop_code_reg IS NOT NULL"
					+ "					 AND active_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
					+ "					 AND active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1 " + province_condition
					+ district_condition + " )" + "GROUP BY   ROLLUP(province_reg, sub_type)";

			//
			// "SELECT province, sub_type, COUNT(sub_id) tot_sub"
			// + " FROM (SELECT sub_id, isdn, active_datetime,
			// province,district"
			// + " , sub_type,emp_code FROM cntt_c6.pre_vms_active"
			// + " WHERE cen_code = 6 AND(kit_id = 4 OR sub_type = 'ETK')"
			// + " AND shop_code IS NOT NULL AND active_datetime >= TO_DATE(?,
			// 'YYYY-MM-DD')"
			// + " AND active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1 " +
			// province_condition + district_condition
			// + " ) GROUP BY ROLLUP (province, sub_type)";

			// syslog(sql);
			prpStm = conn.prepareStatement(sql);
			prpStm.setString(1, AjaxController.YYYY_MM_DD_FORMAT.format(from_date));
			prpStm.setString(2, AjaxController.YYYY_MM_DD_FORMAT.format(from_date));
			prpStm.setString(3, AjaxController.YYYY_MM_DD_FORMAT.format(to_date));

			rs = prpStm.executeQuery();
			List<Map<String, String>> pojoList = new LinkedList<Map<String, String>>();
			Map<String, String> pojo;
			long tot_sub_all = 0, tot_sub = 0;
			while (rs != null && rs.next()) {
				pojo = new TreeMap<String, String>();
				pojo.put("province", rs.getString("province"));
				pojo.put("sub_type", rs.getString("sub_type"));
				pojo.put("tot_sub", rs.getString("tot_sub"));
				tot_sub = rs.getLong("tot_sub");
				tot_sub_all = tot_sub_all + tot_sub;
				pojoList.add(pojo);
			}
			pojo = new TreeMap<String, String>();
			pojo.put("tot_sub_all", String.valueOf(tot_sub_all));
			return pojoList;
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getTinhTrangThueBaoDetail(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		String sql = "";
		try {
			JSONObject jsonObject = new JSONObject(json);
			String from_date = String.valueOf(jsonObject.get("from_date")),
					to_date = String.valueOf(jsonObject.get("to_date")), area = String.valueOf(jsonObject.get("area"));

			String app = String.valueOf(jsonObject.get("app"));
			String type = String.valueOf(jsonObject.get("type"));
			String area_clause = "", act_status_clause = "";
			String sub_type = "ALL";
			if (app.equals("1")) {
				try {
					sub_type = String.valueOf(jsonObject.get("sub_type"));
				} catch (Exception e) {
					sub_type = "ALL";
				}
				if (sub_type == null || sub_type.equals(""))
					sub_type = "ALL";
				if (area == null || area.equals(""))
					area = "ALL";
				conn = getConnection();
				if (type.equals("tt")) {
					sql = "BEGIN delete sub_analyze_tmp;INSERT INTO sub_analyze_tmp(sub_id) SELECT DISTINCT sub_id"
							+ "  FROM out_data.mc_subscriber_mv "
							+ " WHERE (delete_datetime IS NULL OR delete_datetime >= TO_DATE(?, 'YYYY-MM-DD')) and cen_reg = 6"
							+ "  AND(kit_id = 4 OR sub_type = 'ETK') AND shop_code_reg IS NOT NULL"
							+ "  AND active_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
							+ "  AND active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1 AND(province_reg||district_reg like '"
							+ area + "%' OR 'ALL' = ?) " + "  AND(sub_type = ? OR 'ALL' = ?) ;"
							+ "  analyze_sub(TO_DATE(?, 'YYYY-MM-DD'), TO_DATE(?, 'YYYY-MM-DD')); END;";
					prpStm = conn.prepareStatement(sql);

					prpStm.setString(1, from_date);
					prpStm.setString(2, from_date);
					prpStm.setString(3, to_date);
					prpStm.setString(4, area);
					prpStm.setString(5, sub_type);
					prpStm.setString(6, sub_type);
					prpStm.setString(7, from_date);
					prpStm.setString(8, to_date);
					prpStm.execute();
					prpStm.close();
					prpStm = conn.prepareStatement("SELECT * from sub_analyze_tmp_v ORDER BY isdn");
				} else {
					sql = "BEGIN DELETE subscriber_tmp;"
							+ " INSERT INTO subscriber_tmp(sub_id,isdn,sta_datetime,end_datetime,status,act_status)"
							+ " SELECT sub_id,isdn,sta_datetime,end_datetime,status ,act_status FROM subscriber_khdn_ptm_v"
							+ " WHERE sta_datetime >= TO_DATE(?, 'YYYY-MM-DD') "
							+ " AND sta_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1 AND (province || district like '" + area
							+ "%' OR 'ALL' = ?) AND (sub_type = ? OR 'ALL' = ?);"
							+ " thuynt.mb6app.analyze_subscriber_tmp(TO_DATE(?, 'YYYY-MM-DD'),TO_DATE(?, 'YYYY-MM-DD'));END;";
					prpStm = conn.prepareStatement(sql);
					prpStm.setString(1, from_date);
					prpStm.setString(2, to_date);
					prpStm.setString(3, area);
					prpStm.setString(4, sub_type);
					prpStm.setString(5, sub_type);
					prpStm.setString(6, from_date);
					prpStm.setString(7, to_date);
					prpStm.execute();
					prpStm.close();
					sql = "select * from mb6_subscriber_v order by isdn";
					prpStm = conn.prepareStatement(sql);
				}
			} else {
				String act_status = String.valueOf(jsonObject.get("act_status"));
				if (area != null && !area.equals(""))
					area_clause = " and province||district like'" + area + "%'";
				else
					area_clause = " AND province IN ('THO', 'NAN', 'QBI', 'HTI')";
				conn = getConnection();

				if (type.equals("ts")) {

					if (act_status.equals("10"))
						act_status_clause = " and act_status IN ('10', '11', '31', '30') ";
					else if (act_status.equals("20"))
						act_status_clause = " and act_status IN ('20', '21', '22', '12', '32') ";
					else
						act_status_clause = " and act_status='" + act_status + "'";

					sql = "SELECT a.sub_id,a.isdn,a.name,a.pay_full_address"
							+ " ,a.province,a.district,a.shop_code,a.emp_code"
							+ " ,TO_CHAR(sta_datetime, 'DD/MM/YYYY') sta_datetime"
							+ " ,TO_CHAR(ngay_chan, 'DD/MM/YYYY') ngay_chan,bare_shop,b.code"
							+ "  FROM (SELECT a.sub_id,a.isdn,a.name,a.pay_full_address"
							+ " ,a.province,a.district,a.shop_code,a.emp_code,a.sta_datetime"
							+ " ,TO_DATE(SUBSTR(bare_detail, 1, 8), 'YYYYMMDD') ngay_chan"
							+ " ,SUBSTR(bare_detail, 15, INSTR(bare_detail, 'ENDSHOP') - 15) bare_shop"
							+ " ,SUBSTR(bare_detail, INSTR(bare_detail, 'ENDSHOP') + 7) bare_resion_id"
							+ "  FROM out_data.subscriber_v a LEFT JOIN ( SELECT pk_id sub_id"
							+ " ,MAX( TO_CHAR(issue_datetime, 'YYYYMMDDhh24miss') || shop_code"
							+ "  || 'ENDSHOP' || reason_id) bare_detail FROM out_data.action_audit_v"
							+ "  WHERE issue_datetime >= TRUNC(SYSDATE) - 60 AND action_id IN ('06', '07')"
							+ "  GROUP BY pk_id) b ON a.sub_id = b.sub_id  WHERE a.sta_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
							+ "  AND a.sta_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1 AND a.status = 1 " + area_clause
							+ act_status_clause
							+ " ) a LEFT JOIN out_data.reason_v b ON a.bare_resion_id = b.reason_id";
					prpStm = conn.prepareStatement(sql);
					prpStm.setString(1, from_date);
					prpStm.setString(2, to_date);

				} else {
					if (act_status.equals("1"))
						act_status_clause = " and hlr_act_status like '%1%' and hlr_act_status not like '%2%' ";
					else if (act_status.equals("2"))
						act_status_clause = " and hlr_act_status like '%2%'";
					else
						act_status_clause = " and hlr_act_status='" + act_status + "'";

					sql = "SELECT  b.province_reg province,b.district_reg district, b.hlr_isdn,hlr_act_status,hlr_status"
							+ "  FROM	out_data.mc_subscriber_mv b where cen_reg = 6 "
							+ " and (delete_datetime IS NULL OR delete_datetime >= TO_DATE(?, 'YYYY-MM-DD')) AND b.active_datetime >=  TO_DATE(?, 'YYYY-MM-DD') "
							+ " AND b.active_datetime <  TO_DATE(?, 'YYYY-MM-DD') + 1  WHERE	 1 = 1 " + area_clause
							+ act_status_clause;
					prpStm = conn.prepareStatement(sql);
					prpStm.setString(1, from_date);
					prpStm.setString(2, from_date);
					prpStm.setString(3, to_date);
				}
			}
			syslog(sql);

			rs = prpStm.executeQuery();
			return getData(rs, 1, user_name, app);

		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getChanMoMonthly(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String month = String.valueOf(jsonObject.get("month")) + "-01";
			String app = String.valueOf(jsonObject.get("app"));
			String donvi = String.valueOf(jsonObject.get("donvi")), level = String.valueOf(jsonObject.get("level"));
			conn = getConnection();
			String sql = " SELECT province"
					+ " , COUNT(CASE WHEN act_chan = '2C_DEPT' THEN sub_id ELSE NULL END) \"chan 2c nocuoc\" "
					+ " , COUNT(CASE WHEN act_chan = '2C_KHAC' THEN sub_id ELSE NULL END) \"chan 2c khac\""
					+ " , COUNT(CASE WHEN act_chan = '1C_DEPT' THEN sub_id ELSE NULL END) \"chan 1c nocuoc\""
					+ " , COUNT(CASE WHEN act_chan = '1C_KHAC' THEN sub_id ELSE NULL END) \"chan 1c khac\""
					+ " , COUNT(CASE WHEN act_chan = '2C_DEPT' THEN ngay_mo ELSE NULL END) \"mo 2c nocuoc\""
					+ " , COUNT(CASE WHEN act_chan = '2C_KHAC' THEN ngay_mo ELSE NULL END) \"mo 2c khac\""
					+ " , COUNT(CASE WHEN act_chan = '1C_DEPT' THEN ngay_mo ELSE NULL END) \"mo_1c nocuoc\""
					+ " , COUNT(CASE WHEN act_chan = '1C_KHAC' THEN ngay_mo ELSE NULL END) \"mo 1c khac\""
					+ "  FROM cntt_c6.chan_mo_monthly"
					+ "  WHERE ngay_chan >= TO_DATE(?, 'yyyy-mm-dd') AND ngay_chan < ADD_MONTHS(TO_DATE(?, 'yyyy-mm-dd'), 1)"
					+ "  GROUP BY province";
			if (donvi.equals("666666")) {
				if (level.equals("0")) {
					sql = " SELECT COUNT(CASE WHEN act_chan = '2C_DEPT' THEN sub_id ELSE NULL END) \"chan 2c nocuoc\" "
							+ " , COUNT(CASE WHEN act_chan = '2C_KHAC' THEN sub_id ELSE NULL END) \"chan 2c khac\""
							+ " , COUNT(CASE WHEN act_chan = '1C_DEPT' THEN sub_id ELSE NULL END) \"chan 1c nocuoc\""
							+ " , COUNT(CASE WHEN act_chan = '1C_KHAC' THEN sub_id ELSE NULL END) \"chan 1c khac\""
							+ " , COUNT(CASE WHEN act_chan = '2C_DEPT' THEN ngay_mo ELSE NULL END) \"mo 2c nocuoc\""
							+ " , COUNT(CASE WHEN act_chan = '2C_KHAC' THEN ngay_mo ELSE NULL END) \"mo 2c khac\""
							+ " , COUNT(CASE WHEN act_chan = '1C_DEPT' THEN ngay_mo ELSE NULL END) \"mo_1c nocuoc\""
							+ " , COUNT(CASE WHEN act_chan = '1C_KHAC' THEN ngay_mo ELSE NULL END) \"mo 1c khac\""
							+ "  FROM cntt_c6.chan_mo_monthly"
							+ "  WHERE ngay_chan >= TO_DATE(?, 'yyyy-mm-dd') AND ngay_chan < ADD_MONTHS(TO_DATE(?, 'yyyy-mm-dd'), 1)";

				} else if (level.equals("1")) {
					sql = " SELECT province"
							+ " , COUNT(CASE WHEN act_chan = '2C_DEPT' THEN sub_id ELSE NULL END) \"chan 2c nocuoc\" "
							+ " , COUNT(CASE WHEN act_chan = '2C_KHAC' THEN sub_id ELSE NULL END) \"chan 2c khac\""
							+ " , COUNT(CASE WHEN act_chan = '1C_DEPT' THEN sub_id ELSE NULL END) \"chan 1c nocuoc\""
							+ " , COUNT(CASE WHEN act_chan = '1C_KHAC' THEN sub_id ELSE NULL END) \"chan 1c khac\""
							+ " , COUNT(CASE WHEN act_chan = '2C_DEPT' THEN ngay_mo ELSE NULL END) \"mo 2c nocuoc\""
							+ " , COUNT(CASE WHEN act_chan = '2C_KHAC' THEN ngay_mo ELSE NULL END) \"mo 2c khac\""
							+ " , COUNT(CASE WHEN act_chan = '1C_DEPT' THEN ngay_mo ELSE NULL END) \"mo_1c nocuoc\""
							+ " , COUNT(CASE WHEN act_chan = '1C_KHAC' THEN ngay_mo ELSE NULL END) \"mo 1c khac\""
							+ "  FROM cntt_c6.chan_mo_monthly"
							+ "  WHERE ngay_chan >= TO_DATE(?, 'yyyy-mm-dd') AND ngay_chan < ADD_MONTHS(TO_DATE(?, 'yyyy-mm-dd'), 1)"
							+ "  GROUP BY province";

				} else {
					sql = " SELECT province,district"
							+ " , COUNT(CASE WHEN act_chan = '2C_DEPT' THEN sub_id ELSE NULL END) \"chan 2c nocuoc\" "
							+ " , COUNT(CASE WHEN act_chan = '2C_KHAC' THEN sub_id ELSE NULL END) \"chan 2c khac\""
							+ " , COUNT(CASE WHEN act_chan = '1C_DEPT' THEN sub_id ELSE NULL END) \"chan 1c nocuoc\""
							+ " , COUNT(CASE WHEN act_chan = '1C_KHAC' THEN sub_id ELSE NULL END) \"chan 1c khac\""
							+ " , COUNT(CASE WHEN act_chan = '2C_DEPT' THEN ngay_mo ELSE NULL END) \"mo 2c nocuoc\""
							+ " , COUNT(CASE WHEN act_chan = '2C_KHAC' THEN ngay_mo ELSE NULL END) \"mo 2c khac\""
							+ " , COUNT(CASE WHEN act_chan = '1C_DEPT' THEN ngay_mo ELSE NULL END) \"mo_1c nocuoc\""
							+ " , COUNT(CASE WHEN act_chan = '1C_KHAC' THEN ngay_mo ELSE NULL END) \"mo 1c khac\""
							+ "  FROM cntt_c6.chan_mo_monthly"
							+ "  WHERE ngay_chan >= TO_DATE(?, 'yyyy-mm-dd') AND ngay_chan < ADD_MONTHS(TO_DATE(?, 'yyyy-mm-dd'), 1)"
							+ " GROUP BY   province,district";
				}
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, month);
				prpStm.setString(2, month);

			} else if (provinceNumberList.contains(donvi)) {
				if (level.equals("0") || level.equals("1")) {
					sql = " SELECT province"
							+ " , COUNT(CASE WHEN act_chan = '2C_DEPT' THEN sub_id ELSE NULL END) \"chan 2c nocuoc\" "
							+ " , COUNT(CASE WHEN act_chan = '2C_KHAC' THEN sub_id ELSE NULL END) \"chan 2c khac\""
							+ " , COUNT(CASE WHEN act_chan = '1C_DEPT' THEN sub_id ELSE NULL END) \"chan 1c nocuoc\""
							+ " , COUNT(CASE WHEN act_chan = '1C_KHAC' THEN sub_id ELSE NULL END) \"chan 1c khac\""
							+ " , COUNT(CASE WHEN act_chan = '2C_DEPT' THEN ngay_mo ELSE NULL END) \"mo 2c nocuoc\""
							+ " , COUNT(CASE WHEN act_chan = '2C_KHAC' THEN ngay_mo ELSE NULL END) \"mo 2c khac\""
							+ " , COUNT(CASE WHEN act_chan = '1C_DEPT' THEN ngay_mo ELSE NULL END) \"mo_1c nocuoc\""
							+ " , COUNT(CASE WHEN act_chan = '1C_KHAC' THEN ngay_mo ELSE NULL END) \"mo 1c khac\""
							+ "  FROM cntt_c6.chan_mo_monthly"
							+ "  WHERE ngay_chan >= TO_DATE(?, 'yyyy-mm-dd') AND ngay_chan < ADD_MONTHS(TO_DATE(?, 'yyyy-mm-dd'), 1)  "
							+ " AND province =get_province_code(?)  GROUP BY province";

				} else {
					sql = " SELECT province,district "
							+ " , COUNT(CASE WHEN act_chan = '2C_DEPT' THEN sub_id ELSE NULL END) \"chan 2c nocuoc\" "
							+ " , COUNT(CASE WHEN act_chan = '2C_KHAC' THEN sub_id ELSE NULL END) \"chan 2c khac\""
							+ " , COUNT(CASE WHEN act_chan = '1C_DEPT' THEN sub_id ELSE NULL END) \"chan 1c nocuoc\""
							+ " , COUNT(CASE WHEN act_chan = '1C_KHAC' THEN sub_id ELSE NULL END) \"chan 1c khac\""
							+ " , COUNT(CASE WHEN act_chan = '2C_DEPT' THEN ngay_mo ELSE NULL END) \"mo 2c nocuoc\""
							+ " , COUNT(CASE WHEN act_chan = '2C_KHAC' THEN ngay_mo ELSE NULL END) \"mo 2c khac\""
							+ " , COUNT(CASE WHEN act_chan = '1C_DEPT' THEN ngay_mo ELSE NULL END) \"mo_1c nocuoc\""
							+ " , COUNT(CASE WHEN act_chan = '1C_KHAC' THEN ngay_mo ELSE NULL END) \"mo 1c khac\""
							+ "  FROM cntt_c6.chan_mo_monthly"
							+ "  WHERE ngay_chan >= TO_DATE(?, 'yyyy-mm-dd') AND ngay_chan < ADD_MONTHS(TO_DATE(?, 'yyyy-mm-dd'), 1)  "
							+ " AND province =get_province_code(?)  GROUP BY   province,district";

				}
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, month);
				prpStm.setString(2, month);
				prpStm.setString(3, donvi);
			} else {

				sql = " SELECT province,district "
						+ " , COUNT(CASE WHEN act_chan = '2C_DEPT' THEN sub_id ELSE NULL END) \"chan 2c nocuoc\" "
						+ " , COUNT(CASE WHEN act_chan = '2C_KHAC' THEN sub_id ELSE NULL END) \"chan 2c khac\""
						+ " , COUNT(CASE WHEN act_chan = '1C_DEPT' THEN sub_id ELSE NULL END) \"chan 1c nocuoc\""
						+ " , COUNT(CASE WHEN act_chan = '1C_KHAC' THEN sub_id ELSE NULL END) \"chan 1c khac\""
						+ " , COUNT(CASE WHEN act_chan = '2C_DEPT' THEN ngay_mo ELSE NULL END) \"mo 2c nocuoc\""
						+ " , COUNT(CASE WHEN act_chan = '2C_KHAC' THEN ngay_mo ELSE NULL END) \"mo 2c khac\""
						+ " , COUNT(CASE WHEN act_chan = '1C_DEPT' THEN ngay_mo ELSE NULL END) \"mo_1c nocuoc\""
						+ " , COUNT(CASE WHEN act_chan = '1C_KHAC' THEN ngay_mo ELSE NULL END) \"mo 1c khac\""
						+ "  FROM cntt_c6.chan_mo_monthly"
						+ "  WHERE ngay_chan >= TO_DATE(?, 'yyyy-mm-dd') AND ngay_chan < ADD_MONTHS(TO_DATE(?, 'yyyy-mm-dd'), 1)  "
						+ " AND get_district_number(province||district) = ?   GROUP BY   province,district";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, month);
				prpStm.setString(2, month);
				prpStm.setString(3, donvi);
			}

			syslog(sql);

			rs = prpStm.executeQuery();
			return getData(rs, 1, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getChanMoMonthlyDetail(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String month = String.valueOf(jsonObject.get("month")) + "-01";
			String app = String.valueOf(jsonObject.get("app"));
			String donvi = String.valueOf(jsonObject.get("donvi"));
			conn = getConnection();
			String sql = "";
			if (donvi.equals("666666")) {
				sql = " SELECT * FROM cntt_c6.chan_mo_monthly"
						+ "  WHERE ngay_chan >= TO_DATE(?, 'yyyy-mm-dd') AND ngay_chan < ADD_MONTHS(TO_DATE(?, 'yyyy-mm-dd'), 1)";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, month);
				prpStm.setString(2, month);

			} else if (provinceNumberList.contains(donvi)) {
				sql = " SELECT * FROM cntt_c6.chan_mo_monthly"
						+ "  WHERE ngay_chan >= TO_DATE(?, 'yyyy-mm-dd') AND ngay_chan < ADD_MONTHS(TO_DATE(?, 'yyyy-mm-dd'), 1) "
						+ " AND province =get_province_code(?) ";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, month);
				prpStm.setString(2, month);
				prpStm.setString(3, donvi);
			} else {
				sql = " SELECT * FROM cntt_c6.chan_mo_monthly"
						+ "  WHERE ngay_chan >= TO_DATE(?, 'yyyy-mm-dd') AND ngay_chan < ADD_MONTHS(TO_DATE(?, 'yyyy-mm-dd'), 1) "
						+ "  AND get_district_number(province||district) = ? ";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, month);
				prpStm.setString(2, month);
				prpStm.setString(3, donvi);
			}
			rs = prpStm.executeQuery();

			return getData(rs, 1, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	private void buildIsdnTmp(JSONObject jsonObject, Connection conn, int is_gold) throws SQLException {
		PreparedStatement prpStm = null;
		String separator = ";";
		String isdn_list = String.valueOf(jsonObject.get("isdn_list")).replaceAll("[^\\d.]", separator);
		String aliving_date;
		try {
			aliving_date = String.valueOf(jsonObject.get("aliving_date"));
			if (aliving_date == null || aliving_date.equals(""))
				aliving_date = "4000-01-01";

		} catch (Exception e) {
			aliving_date = "4000-01-01";
		}
		String[] subArray = isdn_list.split(separator);

		if (aliving_date == null || aliving_date.equals(""))
			aliving_date = "4000-01-01";

		int k = subArray.length / 15000;
		int d = subArray.length % 15000;
		prpStm = conn.prepareStatement("BEGIN delete sub_act_temp; END;");
		prpStm.execute();

		for (int j = 0; j < k; j++) {
			prpStm = conn.prepareStatement("BEGIN INSERT INTO sub_act_temp(isdn,active_time,is_gold,step) "
					+ "VALUES (?,TO_DATE(?, 'YYYY-MM-DD'),?,0);EXCEPTION WHEN OTHERS THEN	NULL; END;");

			for (int i = j * 15000; i < j * 15000 + 14999; i++) {
				try {
					prpStm.setString(1, subArray[i]);
					prpStm.setString(2, aliving_date);
					prpStm.setInt(3, is_gold);
					prpStm.addBatch();
				} catch (Exception e) {
					syslog(subArray[i] + " Loi o so nay " + e.toString());
				}
			}
			prpStm.executeBatch();
			prpStm.close();
		}

		prpStm = conn.prepareStatement("BEGIN INSERT INTO sub_act_temp(isdn,active_time,is_gold,step) "
				+ "VALUES (?,TO_DATE(?, 'YYYY-MM-DD'),?,0);EXCEPTION WHEN OTHERS THEN	NULL; END;");

		for (int i = k * 15000; i < k * 15000 + d; i++) {
			try {
				prpStm.setString(1, subArray[i]);
				prpStm.setString(2, aliving_date);
				prpStm.setInt(3, is_gold);
				prpStm.addBatch();
			} catch (Exception e) {
				syslog(subArray[i] + " Loi o so nay " + e.toString());
			}
		}
		prpStm.executeBatch();
		prpStm.close();

	}

	@Override
	public List<Map<String, String>> getChatLuongDonHang(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		// Cau lenh lay thue bao tra truoc
		String resultSql = " SELECT * from sub_analyze_tmp_v ORDER BY serial, isdn";

		try {
			JSONObject jsonObject = new JSONObject(json);
			String serial_clause, begin_serial, end_serial, tu_ngay, den_ngay, type;
			tu_ngay = String.valueOf(jsonObject.get("tu_ngay")).replaceAll("-", "");
			if (tu_ngay == null || tu_ngay.equals(""))
				tu_ngay = "40000101";
			den_ngay = String.valueOf(jsonObject.get("den_ngay")).replaceAll("-", "");
			String app = String.valueOf(jsonObject.get("app"));
			try {
				type = String.valueOf(jsonObject.get("type"));
				if (!type.equals("0") && !type.equals("1"))
					type = "-1";
			} catch (Exception e) {
				type = "-1";
			}

			if (type.equals("-1")) {// Danh gia chat luong don hang theo serial
				serial_clause = String.valueOf(jsonObject.get("serial_clause"));
				begin_serial = String.valueOf(jsonObject.get("begin_serial"));
				end_serial = String.valueOf(jsonObject.get("end_serial"));
				if (serial_clause != null && serial_clause.length() > 3)
					serial_clause = serial_clause.substring(3) + "  or serial between '" + begin_serial + "' and '"
							+ end_serial + "'";
				else
					serial_clause = "serial between '" + begin_serial + "' and '" + end_serial + "'";
				String sqlBlock = "BEGIN delete sub_analyze_tmp; INSERT INTO sub_analyze_tmp(serial, isdn, sub_id)"
						+ " WITH seri_sim AS (SELECT * FROM out_data.stock_sim_v WHERE (" + serial_clause
						+ " )) SELECT a.serial, b.hlr_isdn, b.sub_id FROM seri_sim a"
						+ "  LEFT JOIN out_data.mc_subscriber_mv b ON a.imsi = b.hlr_imsi and (delete_datetime IS NULL) ;"
						+ " analyze_sub(TO_DATE(?, 'YYYYMMDD'), TO_DATE(?, 'YYYYMMDD'));END;";
				// syslog(sqlBlock);
				// .replaceAll("\n", " ")
				conn = getConnection();
				prpStm = conn.prepareStatement(sqlBlock);
				prpStm.setString(1, tu_ngay);
				prpStm.setString(2, den_ngay);
				prpStm.execute();
				prpStm.close();

			} else {// Danh gia chat luong thue bao

				conn = getConnection();
				conn.setAutoCommit(false);
				buildIsdnTmp(jsonObject, conn, Integer.valueOf(type));

				prpStm = conn.prepareStatement("BEGIN mb6app.pre_assign_sub_reg3; END;");
				prpStm.execute();
				prpStm.close();
				if (type.equals("0")) {// tra truoc
					prpStm = conn.prepareStatement("BEGIN delete sub_analyze_tmp; INSERT INTO sub_analyze_tmp(sub_id)"
							+ " SELECT DISTINCT sub_id FROM sub_act_temp WHERE step = 1 AND is_gold = 0;"
							+ " analyze_sub(TO_DATE(?, 'YYYYMMDD'), TO_DATE(?, 'YYYYMMDD')); END;");
					prpStm.setString(1, tu_ngay);
					prpStm.setString(2, den_ngay);
					prpStm.execute();
				} else {// tra sau
					prpStm = conn.prepareStatement(
							"BEGIN delete subscriber_tmp; FOR v IN (SELECT DISTINCT sub_id FROM sub_act_temp"
									+ "  WHERE step = 1 AND is_gold = 1 AND sub_id IS NOT NULL)LOOP"
									+ " INSERT INTO subscriber_tmp(sub_id,isdn,sta_datetime,end_datetime"
									+ " ,status,act_status)SELECT sub_id,isdn,sta_datetime,end_datetime"
									+ " ,status,act_status FROM out_data.subscriber_v WHERE sub_id = v.sub_id;"
									+ " END LOOP;thuynt.mb6app.analyze_subscriber_tmp(TO_DATE(?, 'YYYYMMDD'), TO_DATE(?, 'YYYYMMDD'));END;");
					prpStm.setString(1, tu_ngay);
					prpStm.setString(2, den_ngay);
					prpStm.execute();
					// Cau lenh lay thue bao tra sau
					resultSql = "select * from mb6_subscriber_v order by isdn";
				}

			}
			prpStm = conn.prepareStatement(resultSql);
			rs = prpStm.executeQuery();
			return getData(rs, 1, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}

	}

	@Override
	public List<Map<String, String>> getStockIsdnDetail(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			conn = getConnection();
			buildIsdnTmp(jsonObject, conn, 0);
			String app = String.valueOf(jsonObject.get("app"));
			String sql = "select * from out_data.stock_isdn_donle_v WHERE isdn in (select isdn from sub_act_temp)";
			prpStm = conn.prepareStatement(sql);
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getKhdnNewActiveDetail(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String from_date = String.valueOf(jsonObject.get("from_date")),
					to_date = String.valueOf(jsonObject.get("to_date")),
					province = String.valueOf(jsonObject.get("province")),
					sub_type = String.valueOf(jsonObject.get("sub_type"));
			String app = String.valueOf(jsonObject.get("app"));
			conn = getConnection();

			prpStm = conn.prepareStatement(
					"BEGIN delete sub_analyze_tmp;INSERT INTO sub_analyze_tmp(sub_id) SELECT DISTINCT sub_id"
							+ "  FROM out_data.mc_subscriber_mv WHERE cen_reg = 6"
							+ "  AND active_datetime >= TO_DATE('2015-01-01', 'YYYY-MM-DD')"
							+ "  AND(kit_id = 4 OR sub_type = 'ETK') AND shop_code_reg IS NOT NULL"
							+ "  AND active_datetime >= TO_DATE(?, 'YYYY-MM-DD')"
							+ "  AND active_datetime < TO_DATE(?, 'YYYY-MM-DD') + 1 AND(province_reg = ? OR 'ALL' = ?) "
							+ "  AND(sub_type = ? OR 'ALL' = ?) ;"
							+ "  analyze_sub(TO_DATE(?, 'YYYY-MM-DD'), TO_DATE(?, 'YYYY-MM-DD')); END;");

			prpStm.setString(1, from_date);
			prpStm.setString(2, to_date);
			prpStm.setString(3, province);
			prpStm.setString(4, province);
			prpStm.setString(5, sub_type);
			prpStm.setString(6, sub_type);
			prpStm.setString(7, from_date);
			prpStm.setString(8, to_date);

			prpStm.execute();
			prpStm.close();
			prpStm = conn.prepareStatement("SELECT * from sub_analyze_tmp_v ORDER BY isdn");
			rs = prpStm.executeQuery();
			return getData(rs, 1, user_name, app);

		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getDonViKhSxkd(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		JSONObject jsonObject = new JSONObject(json);
		String app = String.valueOf(jsonObject.get("app"));
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			if (filterBuiledMap.get(user_name + ".." + app) == null)
				buildFilter(user_name, app);
			Mb6Fillter mb6Fillter = filterBuiledMap.get(user_name + ".." + app);
			String province_condition = "", district_condition = "";

			if (!mb6Fillter.isAllProvince()) {
				for (String provin : mb6Fillter.getProvince_numberList()) {
					province_condition = province_condition + " , '" + provin + "'";
				}
				try {
					province_condition = " and province_number in (" + province_condition.substring(2) + " )";
				} catch (Exception e) {
					province_condition = "";
				}
				if (!mb6Fillter.isAllDistrict()) {
					for (String district : mb6Fillter.getDistrict_numberList()) {
						district_condition = district_condition + " , '" + district + "'";
					}
					try {
						district_condition = " and area_code in (" + district_condition.substring(2) + " )";
					} catch (Exception e) {
						district_condition = "";
					}

				}
			} else {
				district_condition = " and area_code in (666666";
				for (String s : this.provinceNumberList) {
					district_condition = district_condition + "," + s;
				}
				district_condition = district_condition + ")";
			}
			String sql = "SELECT area_code, name FROM ("
					+ "  SELECT 666666 area_code, 'Toàn công ty' name, 666666 province_number FROM dual union all"
					+ "  SELECT DISTINCT province_number area_code, province name, province_number"
					+ "  FROM address WHERE center_from_2015 = " + ct + " UNION ALL"
					+ "  SELECT DISTINCT district_number area_code, district, province_number"
					+ "  FROM address WHERE center_from_2015 = " + ct + ") WHERE 1 = 1" + province_condition
					+ district_condition + " ORDER BY case when area_code=666666 then -1 else area_code end ";
			syslog(sql);
			prpStm = conn.prepareStatement(sql);
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getKhsxkd(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String donvi = String.valueOf(jsonObject.get("donvi")), tab = String.valueOf(jsonObject.get("tab"));
			conn = getConnection();
			String sql;
			if (tab.equals("#tab1"))
				sql = "select a.act_type,a.html_des chi_tieu,Nam2016, THANG1, THANG2, THANG3, THANG4, THANG5, THANG6, THANG7,"
						+ " THANG8, THANG9, THANG10, THANG11, THANG12 from mb6_kehoach_type a left join"
						+ " (select act_type,sum(nvl(case when dest_time='2016' then kh else 0 end,0)) Nam2016"
						+ " ,sum(nvl(case when dest_time='201601' then kh else 0 end,0)) Thang1"
						+ " ,sum(nvl(case when dest_time='201602' then kh else 0 end,0)) Thang2"
						+ " ,sum(nvl(case when dest_time='201603' then kh else 0 end,0)) Thang3"
						+ " ,sum(nvl(case when dest_time='201604' then kh else 0 end,0)) Thang4"
						+ " ,sum(nvl(case when dest_time='201605' then kh else 0 end,0)) Thang5"
						+ " ,sum(nvl(case when dest_time='201606' then kh else 0 end,0)) Thang6"
						+ " ,sum(nvl(case when dest_time='201607' then kh else 0 end,0)) Thang7"
						+ " ,sum(nvl(case when dest_time='201608' then kh else 0 end,0)) Thang8"
						+ " ,sum(nvl(case when dest_time='201609' then kh else 0 end,0)) Thang9"
						+ " ,sum(nvl(case when dest_time='201610' then kh else 0 end,0)) Thang10"
						+ " ,sum(nvl(case when dest_time='201611' then kh else 0 end,0)) Thang11"
						+ " ,sum(nvl(case when dest_time='201612' then kh else 0 end,0)) Thang12"
						+ "  from mb6_kehoach_khdn_active where area_number=? and dest_time like '2016%'"
						+ "  and length(dest_time)<=6 group by act_type) b on a.act_type=b.act_type order by stt";
			else
				sql = "select a.act_type,a.html_des chi_tieu,Nam2016, THANG1, THANG2, THANG3, THANG4, THANG5, THANG6, THANG7,"
						+ " THANG8, THANG9, THANG10, THANG11, THANG12 from mb6_kehoach_type a left join"
						+ " (select act_type,sum(nvl(case when dest_time='2016' then khpd else 0 end,0)) Nam2016"
						+ " ,sum(nvl(case when dest_time='201601' then khpd else 0 end,0)) Thang1"
						+ " ,sum(nvl(case when dest_time='201602' then khpd else 0 end,0)) Thang2"
						+ " ,sum(nvl(case when dest_time='201603' then khpd else 0 end,0)) Thang3"
						+ " ,sum(nvl(case when dest_time='201604' then khpd else 0 end,0)) Thang4"
						+ " ,sum(nvl(case when dest_time='201605' then khpd else 0 end,0)) Thang5"
						+ " ,sum(nvl(case when dest_time='201606' then khpd else 0 end,0)) Thang6"
						+ " ,sum(nvl(case when dest_time='201607' then khpd else 0 end,0)) Thang7"
						+ " ,sum(nvl(case when dest_time='201608' then khpd else 0 end,0)) Thang8"
						+ " ,sum(nvl(case when dest_time='201609' then khpd else 0 end,0)) Thang9"
						+ " ,sum(nvl(case when dest_time='201610' then khpd else 0 end,0)) Thang10"
						+ " ,sum(nvl(case when dest_time='201611' then khpd else 0 end,0)) Thang11"
						+ " ,sum(nvl(case when dest_time='201612' then khpd else 0 end,0)) Thang12"
						+ "  from mb6_kehoach_khdn_active where area_number=? and dest_time like '2016%'"
						+ "  and length(dest_time)<=6 group by act_type) b on a.act_type=b.act_type order by stt";
			prpStm = conn.prepareStatement(sql);
			prpStm.setString(1, donvi);
			rs = prpStm.executeQuery();

			ResultSetMetaData rsMetaData = rs.getMetaData();
			int numberOfColumns = rsMetaData.getColumnCount();

			Map<String, String> pojo;
			List<Map<String, String>> pojoList = new LinkedList<Map<String, String>>();
			// put header
			pojo = new TreeMap<String, String>();
			for (int i = 2; i <= numberOfColumns; i++) {
				pojo.put(String.format("%03d", i), "<th>" + rsMetaData.getColumnName(i));
			}
			pojoList.add(pojo);

			while (rs != null && rs.next()) {

				pojo = new TreeMap<String, String>();
				// pojo.put("001", replaceNull("<td id = \"id\" >" +
				// rs.getString("act_type")));
				pojo.put("002", replaceNull(
						"<td id = \"id\" act_type= \"" + rs.getString("act_type") + "\">" + rs.getString("chi_tieu")));
				pojo.put("003", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Nam2016\" >" + rs.getString("Nam2016")));
				pojo.put("004", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang1\" >" + rs.getString("Thang1")));
				pojo.put("005", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang2\" >" + rs.getString("Thang2")));
				pojo.put("006", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang3\" >" + rs.getString("Thang3")));
				pojo.put("007", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang4\" >" + rs.getString("Thang4")));
				pojo.put("008", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang5\" >" + rs.getString("Thang5")));
				pojo.put("009", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang6\" >" + rs.getString("Thang6")));
				pojo.put("010", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang7\" >" + rs.getString("Thang7")));
				pojo.put("011", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang8\" >" + rs.getString("Thang8")));
				pojo.put("012", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang9\" >" + rs.getString("Thang9")));
				pojo.put("013", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang10\" >" + rs.getString("Thang10")));
				pojo.put("014", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang11\" >" + rs.getString("Thang11")));
				pojo.put("015", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang12\" >" + rs.getString("Thang12")));

				pojoList.add(pojo);

			}
			return pojoList;

		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> updateKhsxkd(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String donvi = String.valueOf(jsonObject.get("donvi"));
			String tab = String.valueOf(jsonObject.get("tab"));
			JSONArray kh = new JSONArray(String.valueOf(jsonObject.get("kh")));
			JSONArray khpd = new JSONArray(String.valueOf(jsonObject.get("khpd")));

			conn = getConnection();
			conn.setAutoCommit(false);
			String sql;
			TreeMap<String, TreeMap<String, String>> khAray = new TreeMap<String, TreeMap<String, String>>();
			TreeMap<String, String> khMap;
			if (kh.length() > 0)
				for (int i = 0; i < kh.length(); i++) {
					jsonObject = new JSONObject(kh.get(i).toString());
					khMap = new TreeMap<String, String>();
					khMap.put("0", String.valueOf(jsonObject.get("nam2016")));
					khMap.put("1", String.valueOf(jsonObject.get("thang1")));
					khMap.put("2", String.valueOf(jsonObject.get("thang2")));
					khMap.put("3", String.valueOf(jsonObject.get("thang3")));
					khMap.put("4", String.valueOf(jsonObject.get("thang4")));
					khMap.put("5", String.valueOf(jsonObject.get("thang5")));
					khMap.put("6", String.valueOf(jsonObject.get("thang6")));
					khMap.put("7", String.valueOf(jsonObject.get("thang7")));
					khMap.put("8", String.valueOf(jsonObject.get("thang8")));
					khMap.put("9", String.valueOf(jsonObject.get("thang9")));
					khMap.put("10", String.valueOf(jsonObject.get("thang10")));
					khMap.put("11", String.valueOf(jsonObject.get("thang11")));
					khMap.put("12", String.valueOf(jsonObject.get("thang12")));
					khAray.put(String.valueOf(jsonObject.get("ACT_TYPE")), khMap);
				}
			else {
				sql = "select a.act_type,a.html_des chi_tieu,Nam2016, THANG1, THANG2, THANG3, THANG4, THANG5, THANG6, THANG7,"
						+ " THANG8, THANG9, THANG10, THANG11, THANG12 from mb6_kehoach_type a left join"
						+ " (select act_type,sum(nvl(case when dest_time='2016' then kh else 0 end,0)) Nam2016"
						+ " ,sum(nvl(case when dest_time='201601' then kh else 0 end,0)) Thang1"
						+ " ,sum(nvl(case when dest_time='201602' then kh else 0 end,0)) Thang2"
						+ " ,sum(nvl(case when dest_time='201603' then kh else 0 end,0)) Thang3"
						+ " ,sum(nvl(case when dest_time='201604' then kh else 0 end,0)) Thang4"
						+ " ,sum(nvl(case when dest_time='201605' then kh else 0 end,0)) Thang5"
						+ " ,sum(nvl(case when dest_time='201606' then kh else 0 end,0)) Thang6"
						+ " ,sum(nvl(case when dest_time='201607' then kh else 0 end,0)) Thang7"
						+ " ,sum(nvl(case when dest_time='201608' then kh else 0 end,0)) Thang8"
						+ " ,sum(nvl(case when dest_time='201609' then kh else 0 end,0)) Thang9"
						+ " ,sum(nvl(case when dest_time='201610' then kh else 0 end,0)) Thang10"
						+ " ,sum(nvl(case when dest_time='201611' then kh else 0 end,0)) Thang11"
						+ " ,sum(nvl(case when dest_time='201612' then kh else 0 end,0)) Thang12"
						+ "  from mb6_kehoach_khdn_active where area_number=? and dest_time like '2016%'"
						+ "  and length(dest_time)<=6 group by act_type) b on a.act_type=b.act_type order by stt";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, donvi);
				rs = prpStm.executeQuery();
				while (rs != null && rs.next()) {
					khMap = new TreeMap<String, String>();
					khMap.put("0", rs.getString("Nam2016"));
					khMap.put("1", rs.getString("thang1"));
					khMap.put("2", rs.getString("thang2"));
					khMap.put("3", rs.getString("thang3"));
					khMap.put("4", rs.getString("thang4"));
					khMap.put("5", rs.getString("thang5"));
					khMap.put("6", rs.getString("thang6"));
					khMap.put("7", rs.getString("thang7"));
					khMap.put("8", rs.getString("thang8"));
					khMap.put("9", rs.getString("thang9"));
					khMap.put("10", rs.getString("thang10"));
					khMap.put("11", rs.getString("thang11"));
					khMap.put("12", rs.getString("thang12"));
					khAray.put(rs.getString("ACT_TYPE"), khMap);
				}
				rs.close();
				prpStm.close();
			}

			TreeMap<String, TreeMap<String, String>> khpdAray = new TreeMap<String, TreeMap<String, String>>();
			if (khpd.length() > 0)
				for (int i = 0; i < khpd.length(); i++) {
					jsonObject = new JSONObject(khpd.get(i).toString());
					khMap = new TreeMap<String, String>();
					khMap.put("0", String.valueOf(jsonObject.get("nam2016")));
					khMap.put("1", String.valueOf(jsonObject.get("thang1")));
					khMap.put("2", String.valueOf(jsonObject.get("thang2")));
					khMap.put("3", String.valueOf(jsonObject.get("thang3")));
					khMap.put("4", String.valueOf(jsonObject.get("thang4")));
					khMap.put("5", String.valueOf(jsonObject.get("thang5")));
					khMap.put("6", String.valueOf(jsonObject.get("thang6")));
					khMap.put("7", String.valueOf(jsonObject.get("thang7")));
					khMap.put("8", String.valueOf(jsonObject.get("thang8")));
					khMap.put("9", String.valueOf(jsonObject.get("thang9")));
					khMap.put("10", String.valueOf(jsonObject.get("thang10")));
					khMap.put("11", String.valueOf(jsonObject.get("thang11")));
					khMap.put("12", String.valueOf(jsonObject.get("thang12")));
					khpdAray.put(String.valueOf(jsonObject.get("ACT_TYPE")), khMap);
					// if
					// (String.valueOf(jsonObject.get("ACT_TYPE")).equals("TB_PTM"))
					// {
					// syslog(String.valueOf(jsonObject.get("nam2016")));
					// syslog(khMap.get("0"));
					//
					// }
				}
			else {
				sql = "select a.act_type,a.html_des chi_tieu,Nam2016, THANG1, THANG2, THANG3, THANG4, THANG5, THANG6, THANG7,"
						+ " THANG8, THANG9, THANG10, THANG11, THANG12 from mb6_kehoach_type a left join"
						+ " (select act_type,sum(nvl(case when dest_time='2016' then khpd else 0 end,0)) Nam2016"
						+ " ,sum(nvl(case when dest_time='201601' then khpd else 0 end,0)) Thang1"
						+ " ,sum(nvl(case when dest_time='201602' then khpd else 0 end,0)) Thang2"
						+ " ,sum(nvl(case when dest_time='201603' then khpd else 0 end,0)) Thang3"
						+ " ,sum(nvl(case when dest_time='201604' then khpd else 0 end,0)) Thang4"
						+ " ,sum(nvl(case when dest_time='201605' then khpd else 0 end,0)) Thang5"
						+ " ,sum(nvl(case when dest_time='201606' then khpd else 0 end,0)) Thang6"
						+ " ,sum(nvl(case when dest_time='201607' then khpd else 0 end,0)) Thang7"
						+ " ,sum(nvl(case when dest_time='201608' then khpd else 0 end,0)) Thang8"
						+ " ,sum(nvl(case when dest_time='201609' then khpd else 0 end,0)) Thang9"
						+ " ,sum(nvl(case when dest_time='201610' then khpd else 0 end,0)) Thang10"
						+ " ,sum(nvl(case when dest_time='201611' then khpd else 0 end,0)) Thang11"
						+ " ,sum(nvl(case when dest_time='201612' then khpd else 0 end,0)) Thang12"
						+ "  from mb6_kehoach_khdn_active where area_number=? and dest_time like '2016%'"
						+ "  and length(dest_time)<=6 group by act_type) b on a.act_type=b.act_type order by stt";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, donvi);
				rs = prpStm.executeQuery();
				while (rs != null && rs.next()) {
					khMap = new TreeMap<String, String>();
					khMap.put("0", rs.getString("Nam2016"));
					khMap.put("1", rs.getString("thang1"));
					khMap.put("2", rs.getString("thang2"));
					khMap.put("3", rs.getString("thang3"));
					khMap.put("4", rs.getString("thang4"));
					khMap.put("5", rs.getString("thang5"));
					khMap.put("6", rs.getString("thang6"));
					khMap.put("7", rs.getString("thang7"));
					khMap.put("8", rs.getString("thang8"));
					khMap.put("9", rs.getString("thang9"));
					khMap.put("10", rs.getString("thang10"));
					khMap.put("11", rs.getString("thang11"));
					khMap.put("12", rs.getString("thang12"));
					khpdAray.put(rs.getString("ACT_TYPE"), khMap);
				}
				rs.close();
				prpStm.close();
			}

			String[] xmlAray = new String[13];
			for (int i = 0; i < 13; i++) {
				xmlAray[i] = "";
			}

			for (String key : khAray.keySet()) {
				for (int i = 0; i < 13; i++) {

					xmlAray[i] = xmlAray[i] + " <act t=\"" + key + "\" kh=\"" + khAray.get(key).get(String.valueOf(i))
							+ "\" khpd=\"" + khpdAray.get(key).get(String.valueOf(i)) + "\"/>";
					// if (i == 0) {
					// syslog(xmlAray[i]);
					// }

				}
			}

			for (int i = 0; i < 13; i++) {
				xmlAray[i] = "<acts>" + xmlAray[i] + " </acts>";
			}

			prpStm = conn.prepareStatement("BEGININSERT INTO mb6_kehoach(area_number, dest_time, analyze_data)"
					+ "  VALUES (?, ?, xmltype(?));EXCEPTIONWHEN OTHERSTHEN"
					+ " UPDATE mb6_kehoach SET analyze_data = xmltype(?)"
					+ "  WHERE area_number = ? AND dest_time = ?;END;");
			prpStm.setString(1, donvi);
			prpStm.setString(2, "2016");
			prpStm.setString(3, xmlAray[0]);
			prpStm.setString(4, xmlAray[0]);
			prpStm.setString(5, donvi);
			prpStm.setString(6, "2016");
			prpStm.addBatch();
			for (int i = 1; i < 13; i++) {
				prpStm.setString(1, donvi);
				prpStm.setString(2, "2016" + String.format("%02d", i));
				prpStm.setString(3, xmlAray[i]);
				prpStm.setString(4, xmlAray[i]);
				prpStm.setString(5, donvi);
				prpStm.setString(6, "2016" + String.format("%02d", i));
				prpStm.addBatch();
			}
			prpStm.executeBatch();
			prpStm.close();
			conn.commit();

			if (tab.equals("#tab1"))
				sql = "select a.act_type,a.html_des chi_tieu,Nam2016, THANG1, THANG2, THANG3, THANG4, THANG5, THANG6, THANG7,"
						+ " THANG8, THANG9, THANG10, THANG11, THANG12 from mb6_kehoach_type a left join"
						+ " (select act_type,sum(nvl(case when dest_time='2016' then kh else 0 end,0)) Nam2016"
						+ " ,sum(nvl(case when dest_time='201601' then kh else 0 end,0)) Thang1"
						+ " ,sum(nvl(case when dest_time='201602' then kh else 0 end,0)) Thang2"
						+ " ,sum(nvl(case when dest_time='201603' then kh else 0 end,0)) Thang3"
						+ " ,sum(nvl(case when dest_time='201604' then kh else 0 end,0)) Thang4"
						+ " ,sum(nvl(case when dest_time='201605' then kh else 0 end,0)) Thang5"
						+ " ,sum(nvl(case when dest_time='201606' then kh else 0 end,0)) Thang6"
						+ " ,sum(nvl(case when dest_time='201607' then kh else 0 end,0)) Thang7"
						+ " ,sum(nvl(case when dest_time='201608' then kh else 0 end,0)) Thang8"
						+ " ,sum(nvl(case when dest_time='201609' then kh else 0 end,0)) Thang9"
						+ " ,sum(nvl(case when dest_time='201610' then kh else 0 end,0)) Thang10"
						+ " ,sum(nvl(case when dest_time='201611' then kh else 0 end,0)) Thang11"
						+ " ,sum(nvl(case when dest_time='201612' then kh else 0 end,0)) Thang12"
						+ "  from mb6_kehoach_khdn_active where area_number=? and dest_time like '2016%'"
						+ "  and length(dest_time)<=6 group by act_type) b on a.act_type=b.act_type order by stt";
			else
				sql = "select a.act_type,a.html_des chi_tieu,Nam2016, THANG1, THANG2, THANG3, THANG4, THANG5, THANG6, THANG7,"
						+ " THANG8, THANG9, THANG10, THANG11, THANG12 from mb6_kehoach_type a left join"
						+ " (select act_type,sum(nvl(case when dest_time='2016' then khpd else 0 end,0)) Nam2016"
						+ " ,sum(nvl(case when dest_time='201601' then khpd else 0 end,0)) Thang1"
						+ " ,sum(nvl(case when dest_time='201602' then khpd else 0 end,0)) Thang2"
						+ " ,sum(nvl(case when dest_time='201603' then khpd else 0 end,0)) Thang3"
						+ " ,sum(nvl(case when dest_time='201604' then khpd else 0 end,0)) Thang4"
						+ " ,sum(nvl(case when dest_time='201605' then khpd else 0 end,0)) Thang5"
						+ " ,sum(nvl(case when dest_time='201606' then khpd else 0 end,0)) Thang6"
						+ " ,sum(nvl(case when dest_time='201607' then khpd else 0 end,0)) Thang7"
						+ " ,sum(nvl(case when dest_time='201608' then khpd else 0 end,0)) Thang8"
						+ " ,sum(nvl(case when dest_time='201609' then khpd else 0 end,0)) Thang9"
						+ " ,sum(nvl(case when dest_time='201610' then khpd else 0 end,0)) Thang10"
						+ " ,sum(nvl(case when dest_time='201611' then khpd else 0 end,0)) Thang11"
						+ " ,sum(nvl(case when dest_time='201612' then khpd else 0 end,0)) Thang12"
						+ "  from mb6_kehoach_khdn_active where area_number=? and dest_time like '2016%'"
						+ "  and length(dest_time)<=6 group by act_type) b on a.act_type=b.act_type order by stt";
			prpStm = conn.prepareStatement(sql);
			prpStm.setString(1, donvi);
			rs = prpStm.executeQuery();

			ResultSetMetaData rsMetaData = rs.getMetaData();
			int numberOfColumns = rsMetaData.getColumnCount();

			Map<String, String> pojo;
			List<Map<String, String>> pojoList = new LinkedList<Map<String, String>>();
			// put header
			pojo = new TreeMap<String, String>();
			for (int i = 2; i <= numberOfColumns; i++) {
				pojo.put(String.format("%03d", i), "<th>" + rsMetaData.getColumnName(i));
			}
			pojoList.add(pojo);

			while (rs != null && rs.next()) {

				pojo = new TreeMap<String, String>();
				// pojo.put("001", replaceNull("<td id = \"id\" >" +
				// rs.getString("act_type")));
				// pojo.put("002", replaceNull("<td>" +
				// rs.getString("chi_tieu")));
				pojo.put("002", replaceNull("<td id = \"id\" act_type= \"" + rs.getString("act_type")
						+ "\" style=\"width: 200px;\">" + rs.getString("chi_tieu")));
				pojo.put("003", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Nam2016\" >" + rs.getString("Nam2016")));
				pojo.put("004", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang1\" >" + rs.getString("Thang1")));
				pojo.put("005", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang2\" >" + rs.getString("Thang2")));
				pojo.put("006", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang3\" >" + rs.getString("Thang3")));
				pojo.put("007", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang4\" >" + rs.getString("Thang4")));
				pojo.put("008", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang5\" >" + rs.getString("Thang5")));
				pojo.put("009", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang6\" >" + rs.getString("Thang6")));
				pojo.put("010", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang7\" >" + rs.getString("Thang7")));
				pojo.put("011", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang8\" >" + rs.getString("Thang8")));
				pojo.put("012", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang9\" >" + rs.getString("Thang9")));
				pojo.put("013", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang10\" >" + rs.getString("Thang10")));
				pojo.put("014", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang11\" >" + rs.getString("Thang11")));
				pojo.put("015", replaceNull(
						"<td class=\"canclick thc2\" align = \"right\" id = \"Thang12\" >" + rs.getString("Thang12")));

				pojoList.add(pojo);

			}
			return pojoList;

		} catch (Exception e)

		{
			return getError(e);
		} finally

		{
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getChiPhiTrucTiep(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			conn = getConnection();
			String sql;
			sql = "  SELECT   '<td id=\"id\">' || id id ,'<td>' || pro_name \"TÊN CHƯƠNG TRÌNH\""
					+ "		  ,'<td class=\"editable cnumber\" id=\"cpkh\">' || cpkh \"CHI PHÍ KẾ HOẠCH\""
					+ "		  ,'<td class=\"editable cnumber\" id=\"cptt\">' || cptt \"CHI PHÍ THỰC TẾ\""
					+ "	FROM   mbf6_program WHERE   pro_type = 2 ORDER BY   pro_name DESC";

			prpStm = conn.prepareStatement(sql);
			rs = prpStm.executeQuery();
			return getData2(rs, 0, user_name, "2");

		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> updateChiPhiTrucTiep(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray cp = new JSONArray(String.valueOf(jsonObject.get("cp")));

			conn = getConnection();
			conn.setAutoCommit(false);
			String sql = "update mbf6_program set cpkh=?,cptt=? where id=?";
			prpStm = conn.prepareStatement(sql);

			String id_update = "";
			if (cp.length() > 0) {
				for (int i = 0; i < cp.length(); i++) {
					jsonObject = new JSONObject(cp.get(i).toString());
					prpStm.setString(1, (String) jsonObject.get("cpkh"));
					prpStm.setString(2, (String) jsonObject.get("cptt"));
					prpStm.setString(3, (String) jsonObject.get("id"));
					id_update = id_update + " ," + (String) jsonObject.get("id");
					prpStm.addBatch();

				}
				id_update = "(" + id_update.substring(2) + " )";
				prpStm.executeBatch();
				prpStm.close();
				conn.commit();
			}

			sql = "SELECT ID,pro_name \"Tên chương trình\",cpkh \"Chi phí kế hoạch\",cptt \"Chi phí thực tế\""
					+ " FROM mbf6_program WHERE pro_type=2 and id in " + id_update + "  ORDER BY pro_name DESC";

			sql = "  SELECT   '<td id=\"id\">' || id id ,'<td>' || pro_name \"TÊN CHƯƠNG TRÌNH\""
					+ "		  ,'<td class=\"editable cnumber\" id=\"cpkh\">' || cpkh \"CHI PHÍ KẾ HOẠCH\""
					+ "		  ,'<td class=\"editable cnumber\" id=\"cptt\">' || cptt \"CHI PHÍ THỰC TẾ\""
					+ "	FROM   mbf6_program WHERE   pro_type = 2  and id in " + id_update + "  ORDER BY pro_name DESC";

			syslog(sql);
			prpStm = conn.prepareStatement(sql);
			rs = prpStm.executeQuery();
			return getData2(rs, 0, user_name, "2");

		} catch (Exception e)

		{
			return getError(e);
		} finally

		{
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> updateChiPhiGianTiep(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);

			String cpgt_id = (String) jsonObject.get("cpgt_id");
			String des = (String) jsonObject.get("des");
			String tu_ngay = (String) jsonObject.get("tu_ngay");
			String den_ngay = (String) jsonObject.get("den_ngay");
			String app = String.valueOf(jsonObject.get("app"));
			JSONArray cpgt_area = new JSONArray(String.valueOf(jsonObject.get("cpgt_area")));
			conn = getConnection();
			conn.setAutoCommit(false);
			String sql = "";
			if (cpgt_id.equals("0")) {
				CallableStatement proc = conn.prepareCall(
						"{ ? = call add_chi_phi_gian_tiep(to_date(?,'YYYY-MM-DD'), to_date(?,'YYYY-MM-DD'), ?) }");
				proc.registerOutParameter(1, Types.INTEGER);
				proc.setString(2, tu_ngay);
				proc.setString(3, den_ngay);
				proc.setString(4, des);
				proc.execute();
				cpgt_id = String.valueOf(proc.getInt(1));
			} else {
				sql = "update chi_phi_gian_tiep set from_date=to_date(?,'YYYY-MM-DD'),"
						+ " end_date=to_date(?,'YYYY-MM-DD'), description=? where id=?";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, tu_ngay);
				prpStm.setString(2, den_ngay);
				prpStm.setString(3, des);
				prpStm.setString(4, cpgt_id);
				prpStm.execute();
				prpStm.close();
				if (cpgt_area.length() > 0) {
					sql = "update chi_phi_gian_tiep_area set amount=? where id=? and area_number=?";
					prpStm = conn.prepareStatement(sql);
					for (int i = 0; i < cpgt_area.length(); i++) {
						jsonObject = new JSONObject(cpgt_area.get(i).toString());
						prpStm.setString(1, (String) jsonObject.get("amount"));
						prpStm.setString(2, cpgt_id);
						prpStm.setString(3, (String) jsonObject.get("area_code"));
						prpStm.addBatch();
					}
					prpStm.executeBatch();
					prpStm.close();
				}
			}
			sql = "select " + cpgt_id + " from dual";
			prpStm = conn.prepareStatement(sql);
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);

		} catch (Exception e)

		{
			return getError(e);
		} finally

		{
			try {
				clean(conn, prpStm, rs);
			} catch (Exception e) {

			}
		}
	}

	@Override
	public List<Map<String, String>> getCpgtDesList(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			JSONObject jsonObject = new JSONObject(json);
			String cpgt_id = (String) jsonObject.get("cpgt_id");
			String tu_ngay = (String) jsonObject.get("tu_ngay");
			String den_ngay = (String) jsonObject.get("den_ngay");
			String app = String.valueOf(jsonObject.get("app"));
			if (cpgt_id.equals("0")) {
				prpStm = conn.prepareStatement("select id,description  from chi_phi_gian_tiep"
						+ " where from_date <=to_date(?,'YYYY-MM-DD') and end_date >= to_date(?,'YYYY-MM-DD') order by description desc");

				prpStm.setString(1, den_ngay);
				prpStm.setString(2, tu_ngay);
			} else {
				prpStm = conn.prepareStatement("select id,description  from chi_phi_gian_tiep"
						+ " where from_date <=to_date(?,'YYYY-MM-DD') and end_date >= to_date(?,'YYYY-MM-DD') and id=? "
						+ "order by description desc");
				prpStm.setString(1, den_ngay);
				prpStm.setString(2, tu_ngay);
				prpStm.setString(3, cpgt_id);

			}
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getChiPhiGianTiep(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			prpStm = conn.prepareStatement(
					"select to_char(from_date,'YYYY-MM-DD') from_date,to_char(end_date,'YYYY-MM-DD') end_date from chi_phi_gian_tiep where id=?");
			JSONObject jsonObject = new JSONObject(json);
			String app = String.valueOf(jsonObject.get("app"));
			prpStm.setString(1, (String) jsonObject.get("cpgt_id"));
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}

	}

	@Override
	public List<Map<String, String>> getChiPhiGianTiepArea(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			JSONObject jsonObject = new JSONObject(json);
			String cpgt_id = (String) jsonObject.get("cpgt_id");
			String donvi = (String) jsonObject.get("donvi");
			String level = (String) jsonObject.get("level");
			String sql;
			if (donvi.equals("666666")) {
				if (level.equals("0")) {
					sql = "select '<td>'||id id,'<td id = \"area_number\" >'|| area_number area_number,'<td> Mobifone kv6' don_vi"
							+ " ,'<td class=\"editable cnumber\"  id = \"amount\" >'||amount amount"
							+ " from chi_phi_gian_tiep_area where area_number=666666 and id=?";
				} else if (level.equals("1")) {
					sql = "select '<td>'||id id,'<td id = \"area_number\" >'|| area_number area_number,'<td>'||'MF '||don_vi don_vi"
							+ " ,'<td class=\"editable cnumber\"  id = \"amount\" >'||amount amount"
							+ " from chi_phi_gian_tiep_area a "
							+ "inner join (select distinct province_number,province_bill don_vi from out_data.address "
							+ "where center_from_2015=6) b on a.area_number=b.province_number and id=?";

				} else {
					sql = "select '<td>'||id id,'<td id = \"area_number\" >'|| area_number area_number,'<td>'||'MF '||don_vi don_vi"
							+ " ,'<td class=\"editable cnumber\"  id = \"amount\" >'||amount amount"
							+ " from chi_phi_gian_tiep_area a "
							+ "inner join (select distinct district_number,district_bill||'-'|| province_bill don_vi ,province_number"
							+ " from out_data.address where center_from_2015=6) b on a.area_number=b.district_number and id=? "
							+ "order by province_number";
				}
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, cpgt_id);

			} else if (provinceNumberList.contains(donvi)) {
				if (level.equals("0") || level.equals("1")) {
					sql = "select '<td>'||id id,'<td id = \"area_number\" >'|| area_number area_number,'<td>'||'MF '||don_vi don_vi"
							+ " ,'<td class=\"editable cnumber\"  id = \"amount\" >'||amount amount"
							+ " from chi_phi_gian_tiep_area a "
							+ "inner join (select distinct province_number,province_bill from out_data.address "
							+ "where center_from_2015=6 and province_number=?) b on a.area_number=b.province_number and id=?";
				} else {
					sql = "select '<td>'||id id,'<td id = \"area_number\" >'|| area_number area_number,'<td>'||'MF '||don_vi don_vi"
							+ " ,'<td class=\"editable cnumber\"  id = \"amount\" >'||amount amount"
							+ " from chi_phi_gian_tiep_area a "
							+ "inner join (select distinct district_number,district_bill||'-'|| province_bill don_vi ,province_number"
							+ " from out_data.address where center_from_2015=6 and province_number=?) b on a.area_number=b.district_number and id=?"
							+ "order by b.don_vi";
				}
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, donvi);
				prpStm.setString(2, cpgt_id);
			} else {
				sql = "select '<td>'||id id,'<td id = \"area_number\" >'|| area_number area_number,'<td>'||'MF '||don_vi don_vi"
						+ " ,'<td class=\"editable cnumber\"  id = \"amount\" >'||amount amount"
						+ " from chi_phi_gian_tiep_area a "
						+ "inner join (select distinct district_number,district_bill||'-'|| province_bill don_vi ,province_number"
						+ " from out_data.address where center_from_2015=6 and district_number=?) b on a.area_number=b.district_number and id=?"
						+ "order by b.don_vi";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, donvi);
				prpStm.setString(2, cpgt_id);

			}
			rs = prpStm.executeQuery();
			return getData2(rs, 0, user_name, "5");
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> danhgiaChiPhiTrucTiep(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		String sql;
		boolean luyke = true;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);

			JSONObject jsonObject = new JSONObject(json);
			String thang_kh = (String) jsonObject.get("thang_kh"), donvi = (String) jsonObject.get("donvi"),
					dg_tu_ngay = (String) jsonObject.get("dg_tu_ngay"),
					dg_den_ngay = (String) jsonObject.get("dg_den_ngay"), level = (String) jsonObject.get("level");
			String app = String.valueOf(jsonObject.get("app"));
			if (!dg_tu_ngay.equals(""))
				luyke = false;

			if (luyke) {
				sql = "begin dgcp_luyke(to_date(?,'YYYY-MM-DD'), to_date(?,'YYYY-MM-DD')); end;";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, thang_kh + "-01");
				prpStm.setString(2, dg_den_ngay);
				prpStm.execute();
			} else {
				sql = "begin dgcp(to_date(?,'YYYY-MM-DD'), to_date(?,'YYYY-MM-DD'), to_date(?,'YYYY-MM-DD')); end;";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, thang_kh + "-01");
				prpStm.setString(2, dg_tu_ngay);
				prpStm.setString(3, dg_den_ngay);
				prpStm.execute();
				prpStm.close();
			}

			if (donvi.equals("666666")) {
				if (level.equals("0")) {// Muc don vi dc chon
					sql = " SELECT '<td>' ||a.pro_id pro_id,'<td>' ||b.pro_name pro_name"
							+ " ,'<td>' ||TO_CHAR(b.active_min, 'DD/MM/YYYY') \"active min\""
							+ " ,'<td>' ||TO_CHAR(b.active_max, 'DD/MM/YYYY') \"active max\""
							+ " ,'<td class=\"cnumber\">' || b.cpkh cpkh"
							+ " ,'<td class=\"cnumber\">' || b.cptt cptt,'<td class=\"cnumber\">' || SUM(sltb) sltb"
							+ " ,'<td class=\"cnumber\">' || SUM(dttkc) dttkc,'<td class=\"cnumber\">' || SUM(dtkm) dtkm"
							+ " ,'<td class=\"cnumber\">' || SUM(dtnt) dtnt"
							+ " ,'<td class=\"cnumber\">' || SUM(dttkc_province) \"dttkc province\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtkm_province) \"dtkm province\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtnt_province) \"dtnt province\""
							+ " ,'<td class=\"cnumber\">' || SUM(dttkc_district) \"dttkc district\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtkm_district) \"dtkm district\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtnt_district) \"dtnt district\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_nap_the_1lan) \"stb nap the 1lan\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_nap_the_hon_1lan) \"stb nap the hon 1lan\""
							+ " ,'<td class=\"cnumber\">' || SUM(vlr_last) \"vlr last\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_un25) \"stb vlr un25\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_up25un50) \"stb vlr up25un50\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_up50un75) \"stb vlr up50un75\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_up75) \"stb vlr up75\""
							+ " FROM danh_gia_chi_phi_tmp a INNER JOIN mbf6_program b ON a.pro_id = b.id"
							+ "  GROUP BY a.pro_id,b.pro_name,TO_CHAR(b.active_min, 'DD/MM/YYYY')"
							+ " ,TO_CHAR(b.active_max, 'DD/MM/YYYY'),b.cpkh,b.cptt";

				} else if (level.equals("1")) {// Muc tinh
					sql = "SELECT '<td>' ||a.pro_id pro_id,'<td>' ||b.pro_name pro_name"
							+ ",'<td>' ||a.province province"
							+ " ,'<td>' ||TO_CHAR(b.active_min, 'DD/MM/YYYY') \"active min\""
							+ " ,'<td>' ||TO_CHAR(b.active_max, 'DD/MM/YYYY') \"active max\""
							+ " ,'<td class=\"cnumber\">' || b.cpkh cpkh"
							+ " ,'<td class=\"cnumber\">' || b.cptt cptt,'<td class=\"cnumber\">' || SUM(sltb) sltb"
							+ " ,'<td class=\"cnumber\">' || SUM(dttkc) dttkc,'<td class=\"cnumber\">' || SUM(dtkm) dtkm"
							+ " ,'<td class=\"cnumber\">' || SUM(dtnt) dtnt"
							+ " ,'<td class=\"cnumber\">' || SUM(dttkc_province) \"dttkc province\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtkm_province) \"dtkm province\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtnt_province) \"dtnt province\""
							+ " ,'<td class=\"cnumber\">' || SUM(dttkc_district) \"dttkc district\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtkm_district) \"dtkm district\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtnt_district) \"dtnt district\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_nap_the_1lan) \"stb nap the 1lan\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_nap_the_hon_1lan) \"stb nap the hon 1lan\""
							+ " ,'<td class=\"cnumber\">' || SUM(vlr_last) \"vlr last\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_un25) \"stb vlr un25\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_up25un50) \"stb vlr up25un50\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_up50un75) \"stb vlr up50un75\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_up75) \"stb vlr up75\""
							+ " FROM danh_gia_chi_phi_tmp a INNER JOIN mbf6_program b ON a.pro_id = b.id"
							+ " GROUP BY a.pro_id, b.pro_name, a.province"
							+ ",TO_CHAR(b.active_min, 'DD/MM/YYYY'),TO_CHAR(b.active_max, 'DD/MM/YYYY'),b.cpkh,b.cptt";

				} else if (level.equals("2")) {// Muc huyen
					sql = "SELECT '<td>' ||a.pro_id pro_id,'<td>' ||b.pro_name pro_name"
							+ " ,'<td>' ||a.province province,'<td>' ||a.district district"
							+ " ,'<td>' ||TO_CHAR(b.active_min, 'DD/MM/YYYY') \"active min\""
							+ " ,'<td>' ||TO_CHAR(b.active_max, 'DD/MM/YYYY') \"active max\""
							+ " ,'<td class=\"cnumber\">' || b.cpkh cpkh"
							+ " ,'<td class=\"cnumber\">' || b.cptt cptt,'<td class=\"cnumber\">' || SUM(sltb) sltb"
							+ " ,'<td class=\"cnumber\">' || SUM(dttkc) dttkc,'<td class=\"cnumber\">' || SUM(dtkm) dtkm"
							+ " ,'<td class=\"cnumber\">' || SUM(dtnt) dtnt"
							+ " ,'<td class=\"cnumber\">' || SUM(dttkc_province) \"dttkc province\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtkm_province) \"dtkm province\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtnt_province) \"dtnt province\""
							+ " ,'<td class=\"cnumber\">' || SUM(dttkc_district) \"dttkc district\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtkm_district) \"dtkm district\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtnt_district) \"dtnt district\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_nap_the_1lan) \"stb nap the 1lan\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_nap_the_hon_1lan) \"stb nap the hon 1lan\""
							+ " ,'<td class=\"cnumber\">' || SUM(vlr_last) \"vlr last\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_un25) \"stb vlr un25\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_up25un50) \"stb vlr up25un50\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_up50un75) \"stb vlr up50un75\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_up75) \"stb vlr up75\""
							+ " FROM danh_gia_chi_phi_tmp a INNER JOIN mbf6_program b ON a.pro_id = b.id "
							+ " GROUP BY a.pro_id, b.pro_name, a.province,a.district"
							+ ",TO_CHAR(b.active_min, 'DD/MM/YYYY'),TO_CHAR(b.active_max, 'DD/MM/YYYY'),b.cpkh,b.cptt";

				}
				prpStm = conn.prepareStatement(sql);
			} else if (provinceNumberList.contains(donvi)) { // Tinh
				if (level.equals("0") || level.equals("1")) {// Muc don vi dc
					// chon
					sql = "SELECT '<td>' ||a.pro_id pro_id,'<td>' ||b.pro_name pro_name"
							+ ",'<td>' ||a.province province"
							+ " ,'<td>' ||TO_CHAR(b.active_min, 'DD/MM/YYYY') \"active min\""
							+ " ,'<td>' ||TO_CHAR(b.active_max, 'DD/MM/YYYY') \"active max\""
							+ " ,'<td class=\"cnumber\">' || b.cpkh cpkh"
							+ " ,'<td class=\"cnumber\">' || b.cptt cptt,'<td class=\"cnumber\">' || SUM(sltb) sltb"
							+ " ,'<td class=\"cnumber\">' || SUM(dttkc) dttkc,'<td class=\"cnumber\">' || SUM(dtkm) dtkm"
							+ " ,'<td class=\"cnumber\">' || SUM(dtnt) dtnt"
							+ " ,'<td class=\"cnumber\">' || SUM(dttkc_province) \"dttkc province\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtkm_province) \"dtkm province\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtnt_province) \"dtnt province\""
							+ " ,'<td class=\"cnumber\">' || SUM(dttkc_district) \"dttkc district\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtkm_district) \"dtkm district\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtnt_district) \"dtnt district\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_nap_the_1lan) \"stb nap the 1lan\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_nap_the_hon_1lan) \"stb nap the hon 1lan\""
							+ " ,'<td class=\"cnumber\">' || SUM(vlr_last) \"vlr last\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_un25) \"stb vlr un25\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_up25un50) \"stb vlr up25un50\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_up50un75) \"stb vlr up50un75\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_up75) \"stb vlr up75\""
							+ " FROM danh_gia_chi_phi_tmp a INNER JOIN"
							+ "  mbf6_program b ON a.pro_id = b.id where a.province=GET_PROVINCE_CODE(?) "
							+ " GROUP BY a.pro_id, b.pro_name, a.province"
							+ ",TO_CHAR(b.active_min, 'DD/MM/YYYY'),TO_CHAR(b.active_max, 'DD/MM/YYYY'),b.cpkh,b.cptt";

				} else { // Muc huyen
					sql = "SELECT '<td>' ||a.pro_id pro_id,'<td>' ||b.pro_name pro_name"
							+ ",'<td>' ||a.province province,'<td>' ||a.district district"
							+ " ,'<td>' ||TO_CHAR(b.active_min, 'DD/MM/YYYY') \"active min\""
							+ " ,'<td>' ||TO_CHAR(b.active_max, 'DD/MM/YYYY') \"active max\""
							+ " ,'<td class=\"cnumber\">' || b.cpkh cpkh"
							+ " ,'<td class=\"cnumber\">' || b.cptt cptt,'<td class=\"cnumber\">' || SUM(sltb) sltb"
							+ " ,'<td class=\"cnumber\">' || SUM(dttkc) dttkc,'<td class=\"cnumber\">' || SUM(dtkm) dtkm"
							+ " ,'<td class=\"cnumber\">' || SUM(dtnt) dtnt"
							+ " ,'<td class=\"cnumber\">' || SUM(dttkc_province) \"dttkc province\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtkm_province) \"dtkm province\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtnt_province) \"dtnt province\""
							+ " ,'<td class=\"cnumber\">' || SUM(dttkc_district) \"dttkc district\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtkm_district) \"dtkm district\""
							+ " ,'<td class=\"cnumber\">' || SUM(dtnt_district) \"dtnt district\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_nap_the_1lan) \"stb nap the 1lan\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_nap_the_hon_1lan) \"stb nap the hon 1lan\""
							+ " ,'<td class=\"cnumber\">' || SUM(vlr_last) \"vlr last\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_un25) \"stb vlr un25\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_up25un50) \"stb vlr up25un50\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_up50un75) \"stb vlr up50un75\""
							+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_up75) \"stb vlr up75\""
							+ " FROM danh_gia_chi_phi_tmp a INNER JOIN"
							+ "  mbf6_program b ON a.pro_id = b.id where a.province=GET_PROVINCE_CODE(?) "
							+ " GROUP BY a.pro_id, b.pro_name, a.province,a.district"
							+ ",TO_CHAR(b.active_min, 'DD/MM/YYYY'),TO_CHAR(b.active_max, 'DD/MM/YYYY'),b.cpkh,b.cptt";

				}
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, donvi);
			} else {
				sql = "SELECT '<td>' ||a.pro_id pro_id,'<td>' ||b.pro_name pro_name"
						+ ",'<td>' ||a.province province,'<td>' ||a.district district"
						+ " ,'<td>' ||TO_CHAR(b.active_min, 'DD/MM/YYYY') \"active min\""
						+ " ,'<td>' ||TO_CHAR(b.active_max, 'DD/MM/YYYY') \"active max\""
						+ " ,'<td class=\"cnumber\">' || b.cpkh cpkh"
						+ " ,'<td class=\"cnumber\">' || b.cptt cptt,'<td class=\"cnumber\">' || SUM(sltb) sltb"
						+ " ,'<td class=\"cnumber\">' || SUM(dttkc) dttkc,'<td class=\"cnumber\">' || SUM(dtkm) dtkm"
						+ " ,'<td class=\"cnumber\">' || SUM(dtnt) dtnt"
						+ " ,'<td class=\"cnumber\">' || SUM(dttkc_province) \"dttkc province\""
						+ " ,'<td class=\"cnumber\">' || SUM(dtkm_province) \"dtkm province\""
						+ " ,'<td class=\"cnumber\">' || SUM(dtnt_province) \"dtnt province\""
						+ " ,'<td class=\"cnumber\">' || SUM(dttkc_district) \"dttkc district\""
						+ " ,'<td class=\"cnumber\">' || SUM(dtkm_district) \"dtkm district\""
						+ " ,'<td class=\"cnumber\">' || SUM(dtnt_district) \"dtnt district\""
						+ " ,'<td class=\"cnumber\">' || SUM(stb_nap_the_1lan) \"stb nap the 1lan\""
						+ " ,'<td class=\"cnumber\">' || SUM(stb_nap_the_hon_1lan) \"stb nap the hon 1lan\""
						+ " ,'<td class=\"cnumber\">' || SUM(vlr_last) \"vlr last\""
						+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_un25) \"stb vlr un25\""
						+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_up25un50) \"stb vlr up25un50\""
						+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_up50un75) \"stb vlr up50un75\""
						+ " ,'<td class=\"cnumber\">' || SUM(stb_vlr_up75) \"stb vlr up75\""
						+ " FROM danh_gia_chi_phi_tmp a INNER JOIN"
						+ "  mbf6_program b ON a.pro_id = b.id where a.province||a.district=GET_DISTRICT_CODE(?) "
						+ " GROUP BY a.pro_id, b.pro_name, a.province,a.district"
						+ ",TO_CHAR(b.active_min, 'DD/MM/YYYY'),TO_CHAR(b.active_max, 'DD/MM/YYYY'),b.cpkh,b.cptt";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, donvi);
			}
			rs = prpStm.executeQuery();
			return getData2(rs, 0, user_name, app);

		} catch (Exception e)

		{
			return getError(e);
		} finally

		{
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> danhGiaChiPhiGianTiep(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			JSONObject jsonObject = new JSONObject(json);
			String tu_ngay = (String) jsonObject.get("tu_ngay");
			String den_ngay = (String) jsonObject.get("den_ngay");
			String donvi = (String) jsonObject.get("donvi");
			String level = (String) jsonObject.get("level");
			String app = String.valueOf(jsonObject.get("app"));
			String sql = "begin dgcp_gian_tiep(to_date(?,'YYYY-MM-DD'), to_date(?,'YYYY-MM-DD')); end;";
			prpStm = conn.prepareStatement(sql);
			prpStm.setString(1, tu_ngay);
			prpStm.setString(2, den_ngay);
			prpStm.execute();
			prpStm.close();
			if (donvi.equals("666666")) {
				if (level.equals("0")) {
					sql = "SELECT	 '<td>' || area_number area_number,'<td>' || 'Mobifone kv6' don_vi"
							+ ",'<td class=\"cnumber\">' || cpgt cpgt,'<td class=\"cnumber\">' || cpkh cpkh"
							+ ",'<td class=\"cnumber\">' || cptt cptt,'<td class=\"cnumber\">' || dttkc dttkc"
							+ ",'<td class=\"cnumber\">' || dtkm dtkm,'<td class=\"cnumber\">' || dtnt dtnt "
							+ ",case when  hqtkc>=1 then '<td style=\"background-color:#96cdcd\"> Hiệu quả' else '<td style=\"background-color:red\"> Chưa hiệu quả' end hqtkc"
							+ ",case when  hqkm>=1 then '<td style=\"background-color:#96cdcd\"> Hiệu quả' else '<td style=\"background-color:red\"> Chưa hiệu quả' end hqkm,"
							+ "case when  hqnt>=1 then '<td style=\"background-color:#96cdcd\"> Hiệu quả' else '<td style=\"background-color:red\"> Chưa hiệu quả' end hqnt"
							+ " from danh_gia_chi_phi_tmp_v where area_number=666666";

				} else if (level.equals("1")) {
					sql = "SELECT	 '<td>' || area_number area_number,'<td>' || 'MF ' || don_vi don_vi"
							+ ",'<td class=\"cnumber\">' || cpgt cpgt,'<td class=\"cnumber\">' || cpkh cpkh"
							+ ",'<td class=\"cnumber\">' || cptt cptt,'<td class=\"cnumber\">' || dttkc dttkc"
							+ ",'<td class=\"cnumber\">' || dtkm dtkm,'<td class=\"cnumber\">' || dtnt dtnt "
							+ ",case when  hqtkc>=1 then '<td style=\"background-color:#96cdcd\"> Hiệu quả' else '<td style=\"background-color:red\"> Chưa hiệu quả' end hqtkc"
							+ ",case when  hqkm>=1 then '<td style=\"background-color:#96cdcd\"> Hiệu quả' else '<td style=\"background-color:red\"> Chưa hiệu quả' end hqkm,"
							+ "case when  hqnt>=1 then '<td style=\"background-color:#96cdcd\"> Hiệu quả' else '<td style=\"background-color:red\"> Chưa hiệu quả' end hqnt"
							+ " from danh_gia_chi_phi_tmp_v a"
							+ " inner join (select distinct province_number,province_bill don_vi from out_data.address"
							+ " where center_from_2015=6) b on a.area_number=b.province_number";

				} else {
					sql = "SELECT	 '<td>' || area_number area_number,'<td>' || 'MF ' || don_vi don_vi"
							+ ",'<td class=\"cnumber\">' || cpgt cpgt,'<td class=\"cnumber\">' || cpkh cpkh"
							+ ",'<td class=\"cnumber\">' || cptt cptt,'<td class=\"cnumber\">' || dttkc dttkc"
							+ ",'<td class=\"cnumber\">' || dtkm dtkm,'<td class=\"cnumber\">' || dtnt dtnt "
							+ ",case when  hqtkc>=1 then '<td style=\"background-color:#96cdcd\"> Hiệu quả' else '<td style=\"background-color:red\"> Chưa hiệu quả' end hqtkc"
							+ ",case when  hqkm>=1 then '<td style=\"background-color:#96cdcd\"> Hiệu quả' else '<td style=\"background-color:red\"> Chưa hiệu quả' end hqkm,"
							+ "case when  hqnt>=1 then '<td style=\"background-color:#96cdcd\"> Hiệu quả' else '<td style=\"background-color:red\"> Chưa hiệu quả' end hqnt"
							+ " from danh_gia_chi_phi_tmp_v a"
							+ " inner join (select distinct district_number,district_bill||'-'|| province_bill don_vi ,province_number "
							+ " from out_data.address where center_from_2015=6) b on a.area_number=b.district_number "
							+ " order by province_number";
				}
				prpStm = conn.prepareStatement(sql);

			} else if (provinceNumberList.contains(donvi)) {
				if (level.equals("0") || level.equals("1")) {
					sql = "SELECT	 '<td>' || area_number area_number,'<td>' || 'MF ' || don_vi don_vi"
							+ ",'<td class=\"cnumber\">' || cpgt cpgt,'<td class=\"cnumber\">' || cpkh cpkh"
							+ ",'<td class=\"cnumber\">' || cptt cptt,'<td class=\"cnumber\">' || dttkc dttkc"
							+ ",'<td class=\"cnumber\">' || dtkm dtkm,'<td class=\"cnumber\">' || dtnt dtnt "
							+ ",case when  hqtkc>=1 then '<td style=\"background-color:#96cdcd\"> Hiệu quả' else '<td style=\"background-color:red\"> Chưa hiệu quả' end hqtkc"
							+ ",case when  hqkm>=1 then '<td style=\"background-color:#96cdcd\"> Hiệu quả' else '<td style=\"background-color:red\"> Chưa hiệu quả' end hqkm,"
							+ "case when  hqnt>=1 then '<td style=\"background-color:#96cdcd\"> Hiệu quả' else '<td style=\"background-color:red\"> Chưa hiệu quả' end hqnt"
							+ " from danh_gia_chi_phi_tmp_v a"
							+ " inner join (select distinct province_number,province_bill don_vi from out_data.address"
							+ " where center_from_2015=6 and province_number=?) b on a.area_number=b.province_number";
				} else {
					sql = "SELECT	 '<td>' || area_number area_number,'<td>' || 'MF ' || don_vi don_vi"
							+ ",'<td class=\"cnumber\">' || cpgt cpgt,'<td class=\"cnumber\">' || cpkh cpkh"
							+ ",'<td class=\"cnumber\">' || cptt cptt,'<td class=\"cnumber\">' || dttkc dttkc"
							+ ",'<td class=\"cnumber\">' || dtkm dtkm,'<td class=\"cnumber\">' || dtnt dtnt "
							+ ",case when  hqtkc>=1 then '<td style=\"background-color:#96cdcd\"> Hiệu quả' else '<td style=\"background-color:red\"> Chưa hiệu quả' end hqtkc"
							+ ",case when  hqkm>=1 then '<td style=\"background-color:#96cdcd\"> Hiệu quả' else '<td style=\"background-color:red\"> Chưa hiệu quả' end hqkm,"
							+ "case when  hqnt>=1 then '<td style=\"background-color:#96cdcd\"> Hiệu quả' else '<td style=\"background-color:red\"> Chưa hiệu quả' end hqnt"
							+ " from danh_gia_chi_phi_tmp_v a"
							+ " inner join (select distinct district_number,district_bill||'-'|| province_bill don_vi ,province_number"
							+ " from out_data.address where center_from_2015=6 and province_number=?) b on a.area_number=b.district_number"
							+ " order by b.don_vi";
				}
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, donvi);

			} else {
				sql = "SELECT	 '<td>' || area_number area_number,'<td>' || 'MF ' || don_vi don_vi"
						+ ",'<td class=\"cnumber\">' || cpgt cpgt,'<td class=\"cnumber\">' || cpkh cpkh"
						+ ",'<td class=\"cnumber\">' || cptt cptt,'<td class=\"cnumber\">' || dttkc dttkc"
						+ ",'<td class=\"cnumber\">' || dtkm dtkm,'<td class=\"cnumber\">' || dtnt dtnt "
						+ ",case when  hqtkc>=1 then '<td style=\"background-color:#96cdcd\"> Hiệu quả' else '<td style=\"background-color:red\"> Chưa hiệu quả' end hqtkc"
						+ ",case when  hqkm>=1 then '<td style=\"background-color:#96cdcd\"> Hiệu quả' else '<td style=\"background-color:red\"> Chưa hiệu quả' end hqkm,"
						+ "case when  hqnt>=1 then '<td style=\"background-color:#96cdcd\"> Hiệu quả' else '<td style=\"background-color:red\"> Chưa hiệu quả' end hqnt"
						+ " from danh_gia_chi_phi_tmp_v a"
						+ " inner join (select distinct district_number,district_bill||'-'|| province_bill don_vi ,province_number"
						+ " from out_data.address where center_from_2015=6 and district_number=?) b on a.area_number=b.district_number"
						+ " order by b.don_vi";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, donvi);
			}
			syslog(sql);
			rs = prpStm.executeQuery();
			return getData2(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getAgent(String user_name, String json, int tonghop) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String month = String.valueOf(jsonObject.get("month")),
					bill_cycle_id = String.valueOf(jsonObject.get("bill_cycle_id")),
					param = String.valueOf(jsonObject.get("param"));
			String app = String.valueOf(jsonObject.get("app"));
			conn = getConnection();
			String sql;
			if (tonghop == 1) {
				prpStm = conn.prepareStatement("BEGIN hoa_hong_ttcp.tonghop(to_date(?,'YYYY-MM-DD'), ?); END;");
				prpStm.setString(1, month + "-01");
				prpStm.setInt(2, Integer.parseInt(bill_cycle_id));
				prpStm.execute();
				prpStm.close();
			} else if (tonghop == 2) {
				prpStm = conn.prepareStatement("BEGIN hoa_hong_ttcp.close_cycle(to_date(?,'YYYY-MM-DD'), ?); END;");
				prpStm.setString(1, month + "-01");
				prpStm.setInt(2, Integer.parseInt(bill_cycle_id));
				prpStm.execute();
				prpStm.close();
			}

			if (bill_cycle_id.equals("1"))
				month = pojo.AppParam.YYYY_MM_FORMAT
						.format(new DateTime(pojo.AppParam.YYYY_MM_FORMAT.parse(month + "-01")).dayOfMonth()
								.withMaximumValue().toDate());
			else if (bill_cycle_id.equals("11"))
				month = month + "-10";
			else
				month = month + "-20";
			syslog(month);
			if (param.equals("0")) {
				sql = "SELECT '<td id = \"collection_group_id\" >'||a.collection_group_id id"
						+ "		,'<td>'||b.province province,'<td>'||b.name \"TÊN ÐẠI LÝ\""
						+ "		,'<td>'||TO_CHAR(a.sta_date, 'YYYY-MM-DD') \"NGÀY ÐẦU KỲ\""
						+ "		,'<td id = \"end_date\">'||TO_CHAR(a.end_date, 'YYYY-MM-DD') \"NGÀY CUỐI KỲ\""
						+ "		,'<td class=\"editable cnumber\" id = \"no_dk\" >'||no_dk \"NỢ ÐẦU KỲ\""
						+ "		,'<td class=\"editable cnumber\" id = \"kh_90\" >'||kh_90 \"KH NỢ 90\""
						+ "		,'<td class=\"editable cnumber\" id = \"kh_no_dong_n\" >'||kh_no_dong_n \"KH NỢ ÐỌNG NĂM N\""
						+ "		,'<td class=\"editable cnumber\" id = \"kh_no_dong_n1\" >'||kh_no_dong_n1 \"KH NỢ ÐỌNG N-1\""
						+ "		,'<td class=\"editable cnumber\" id = \"tien_nop_nh\" >'||tien_nop_nh \"TIỀN NỘP NGÂN HÀNG\""
						+ "		,'<td class=\"editable cnumber\" id = \"kh_tttd\" >'||kh_tttd \"KH THU TỰ ÐỘNG\""
						+ "  FROM		 hhtc_dl_thu_cuoc a LEFT JOIN collection_group_v b"
						+ "		 ON a.collection_group_id = b.collection_group_id"
						+ "  WHERE end_date = TO_DATE(?, 'YYYY-MM-DD') order by province, name";
				syslog(sql);
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, month);
				rs = prpStm.executeQuery();
				// return getAgentKH(rs);
				return getData2(rs, 1, user_name, app);
			} else if (param.equals("1")) {
				sql = "SELECT '<td id = \"collection_group_id\" >'||a.collection_group_id id"
						+ "		,'<td>'||b.province province,'<td>'||b.name \"TÊN ÐẠI LÝ\""
						+ "		,'<td>'||TO_CHAR(a.sta_date, 'YYYY-MM-DD') \"NGÀY ÐẦU KỲ\""
						+ "		,'<td id = \"end_date\">'||TO_CHAR(a.end_date, 'YYYY-MM-DD') \"NGÀY CUỐI KỲ\""
						+ ",'<td class=\"editable\" id = \"ma_nv\" >'|| ma_nv \"MÃ NHÂN VIÊN\""
						+ ",'<td class=\"editable\" id = \"ma_ez_daily\" >'|| ma_ez_daily \"MÃ EZ\""
						+ ",'<td class=\"editable\" id = \"hscl\" >'||hscl  \"HỆ SỐ CHẤT LƯỢNG\""
						+ ",'<td class=\"editable\" id = \"dg_nds\" >'||dg_nds \"ĐƠN GIÁ NGOÀI DS\""
						+ ",'<td class=\"editable\" id = \"dg_ndn\" >'||dg_ndn \"ĐƠN GIÁ NĐ NĂM N\""
						+ ",'<td class=\"editable\" id = \"dg_ndn1\" >'||dg_ndn1 \"ĐƠN GIÁ NĐ NĂM N-1\""
						+ "  FROM hhtc_dl_thu_cuoc a LEFT JOIN collection_group_v b "
						+ "  ON a.collection_group_id = b.collection_group_id "
						+ "  WHERE end_date = TO_DATE(?, 'YYYY-MM-DD') order by province, name";
				syslog(sql);
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, month);
				rs = prpStm.executeQuery();
				return getData2(rs, 1, user_name, app);
			} else if (param.equals("3")) {
				sql = "SELECT '<td id = \"collection_group_id\" >'||a.pay_area_code \"MÃ VÙNG\""
						+ "     ,'<td>'||substr(pay_area_code,1,3) province"
						+ "     ,'<td id = \"end_date\">'||TO_CHAR(a.end_date, 'YYYY-MM-DD') \"NGÀY CUỐI KỲ\""
						+ "     ,'<td class=\"editable cnumber\" id = \"vung\" >'||vung \"VÙNG CƯỚC\""
						+ "     ,'<td class=\"cnumber\" id = \"ty_le_thu30\" >'||ty_le_thu30 \"TỶ LỆ THU 30\""
						+ "     ,'<td class=\"cnumber\" id = \"ty_le_no90\" >'||ty_le_no90 \"TỶ LỆ THU 90\""
						+ "     ,'<td class=\"cnumber\" id = \"sl_kh\" >'||sl_kh \"SỐ LƯỢNG KHÁCH HÀNG\""
						+ "  FROM        hhtc_pay_area a"
						+ "  WHERE end_date = TO_DATE(?, 'YYYY-MM-DD') order by pay_area_code";
				syslog(sql);
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, month);
				rs = prpStm.executeQuery();
				// return getAgentKH(rs);
				return getData2(rs, 1, user_name, app);
			} else {
				String mbftinh = String.valueOf(jsonObject.get("mbftinh"));
				sql = "";
				String sql_fix = "SELECT '<td id = \"collection_group_id\" >'||a.collection_group_id id"
						+ "		,'<td>'||b.province province,'<td>'||b.name \"TÊN ÐẠI LÝ\""
						+ "		,'<td>'||TO_CHAR(a.sta_date, 'YYYY-MM-DD') \"NGÀY ÐẦU KỲ\""
						+ "		,'<td id = \"end_date\">'||TO_CHAR(a.end_date, 'YYYY-MM-DD') \"NGÀY CUỐI KỲ\""
						+ ",'<td class=\"canclick cnumber\" id = \"th_90\" >'|| th_90 \"THU 90 NGÀY\""
						+ ",'<td class=\"canclick cnumber\" id = \"th_no_dong_n\" >'|| th_no_dong_n \"NỢ ĐỌNG NĂM N\""
						+ ",'<td class=\"canclick cnumber\" id = \"th_no_dong_n1\" >'|| th_no_dong_n1 \"NỢ ĐỌNG NĂM N-1\""
						+ ",'<td class=\"canclick cnumber\" id = \"tm_thu_tk\" >'|| tm_thu_tk \"TIỀN MẶT THU TRONG KỲ\""
						+ ",'<td class=\"canclick cnumber\" id = \"thu_trong_ds\" >'|| thu_trong_ds \"THU TRONG DS\""
						+ ",'<td class=\"canclick cnumber\" id = \"th_nds\" >'|| th_nds \"THU NGOÀI DS\""
						+ "  FROM hhtc_dl_thu_cuoc a LEFT JOIN collection_group_v b "
						+ "  ON a.collection_group_id = b.collection_group_id "
						+ "  WHERE end_date = TO_DATE(?, 'YYYY-MM-DD')";
				if (mbftinh.equals("666666")) {
					sql = sql_fix + " order by province, name";
					syslog(sql);
					prpStm = conn.prepareStatement(sql);
					prpStm.setString(1, month);
				} else {
					sql = sql_fix + " and b.province = get_province_code(?) order by province, name";
					syslog(sql);
					prpStm = conn.prepareStatement(sql);
					prpStm.setString(1, month);
					prpStm.setString(2, mbftinh);
				}
				rs = prpStm.executeQuery();
				return getData2(rs, 1, user_name, app);
			}

		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> tongHop(String user_name, String json) {
		// TODO Auto-generated method stub
		return getAgent(user_name, json, 1);
	}

	@Override
	public List<Map<String, String>> hhtc_close(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String month = String.valueOf(jsonObject.get("month")),
					bill_cycle_id = String.valueOf(jsonObject.get("bill_cycle_id"));
			conn = getConnection();

			prpStm = conn.prepareStatement("BEGIN hoa_hong_ttcp.close_cycle(to_date(?,'YYYY-MM-DD'), ?); END;");
			prpStm.setString(1, month + "-01");
			prpStm.setInt(2, Integer.parseInt(bill_cycle_id));
			prpStm.execute();
			prpStm.close();
			return null;
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> hhtc_isclosed(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String month = String.valueOf(jsonObject.get("month")),
					bill_cycle_id = String.valueOf(jsonObject.get("bill_cycle_id"));
			String app = String.valueOf(jsonObject.get("app"));
			conn = getConnection();

			prpStm = conn.prepareStatement(
					"SELECT COUNT(1) closed FROM hhtc_cycle WHERE month = to_date(?,'YYYY-MM-DD') AND bill_cycle_id = ? AND status = 1");
			prpStm.setString(1, month + "-01");
			prpStm.setInt(2, Integer.parseInt(bill_cycle_id));
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}

	}

	// @Override
	public List<Map<String, String>> updateAgentParam(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = new JSONArray(String.valueOf(jsonObject.get("updateparam")));

			conn = getConnection();
			conn.setAutoCommit(false);

			// String id, kh_90, kh_no_dong_n, kh_no_dong_n1, no_dk,
			// tien_nop_nh, ma_nv, ma_ez_daily, pay_area_code_man,end_date =
			// null;

			String id, ma_nv, ma_ez_daily, end_date = "", pay_area_code_man, hscl, dg_nds, dg_ndn, dg_ndn1;
			String app = String.valueOf(jsonObject.get("app"));
			String id_where = "and a.collection_group_id in (";
			prpStm = conn.prepareStatement("UPDATE hhtc_dl_thu_cuoc SET   ma_nv = ?,ma_ez_daily =?"
					+ ",pay_area_code_man=?,hscl=?,dg_nds=?,dg_ndn=?,dg_ndn1=?"
					+ "  WHERE collection_group_id = ? AND end_date = TO_DATE(?, 'YYYY-MM-DD')");
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = new JSONObject(jsonArray.get(i).toString());
				id = String.valueOf(jsonObject.get("id"));
				end_date = String.valueOf(jsonObject.get("end_date"));
				ma_nv = String.valueOf(jsonObject.get("ma_nv"));
				ma_ez_daily = String.valueOf(jsonObject.get("ma_ez_daily"));
				pay_area_code_man = String.valueOf(jsonObject.get("pay_area_code_man"));
				hscl = String.valueOf(jsonObject.get("hscl"));
				dg_nds = String.valueOf(jsonObject.get("dg_nds"));
				dg_ndn = String.valueOf(jsonObject.get("dg_ndn"));
				dg_ndn1 = String.valueOf(jsonObject.get("dg_ndn1"));

				prpStm.setString(1, ma_nv);
				prpStm.setString(2, ma_ez_daily);
				prpStm.setString(3, pay_area_code_man);
				prpStm.setString(4, hscl);
				prpStm.setString(5, dg_nds);

				prpStm.setString(6, dg_ndn);
				prpStm.setString(7, dg_ndn1);

				prpStm.setString(8, id);
				prpStm.setString(9, end_date);
				prpStm.addBatch();
				id_where = id_where + id + " ,";
			}
			id_where = id_where + " -1)";
			prpStm.executeBatch();
			conn.commit();
			prpStm.close();
			// List<Map<String, String>> pojoList = new LinkedList<Map<String,
			// String>>();
			String sql = "SELECT '<td id = \"collection_group_id\" >'||a.collection_group_id id"
					+ ",'<td>'||b.province province,'<td>'||b.name \"TÊN ÐẠI LÝ\""
					+ ",'<td>'||TO_CHAR(a.sta_date, 'YYYY-MM-DD') \"NGÀY ÐẦU KỲ\""
					+ ",'<td id = \"end_date\">'||TO_CHAR(a.end_date, 'YYYY-MM-DD') \"NGÀY CUỐI KỲ\""
					+ ",'<td class=\"editable\" id = \"ma_nv\" >'|| ma_nv \"MÃ NHÂN VIÊN\""
					+ ",'<td class=\"editable\" id = \"ma_ez_daily\" >'|| ma_ez_daily \"MÃ EZ\""
					+ ",'<td class=\"editable\" id = \"hscl\" >'||hscl  \"HỆ SỐ CHẤT LƯỢNG\""
					+ ",'<td class=\"editable\" id = \"dg_nds\" >'||dg_nds \"ĐƠN GIÁ NGOÀI DS\""
					+ ",'<td class=\"editable\" id = \"dg_ndn\" >'||dg_ndn \"ĐƠN GIÁ NĐ NĂM N\""
					+ ",'<td class=\"editable\" id = \"dg_ndn1\" >'||dg_ndn1 \"ĐƠN GIÁ NĐ NĂM N-1\""
					+ "  FROM hhtc_dl_thu_cuoc a LEFT JOIN collection_group_v b "
					+ "  ON a.collection_group_id = b.collection_group_id "
					+ "  WHERE end_date = TO_DATE(?, 'YYYY-MM-DD') " + id_where + "  order by province, name";
			prpStm = conn.prepareStatement(sql);

			prpStm.setString(1, end_date);
			rs = prpStm.executeQuery();
			return getData2(rs, 0, user_name, app);

		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	// private List<Map<String, String>> getAgentKH(ResultSet rs) throws
	// SQLException {
	// ResultSetMetaData rsMetaData = rs.getMetaData();
	// int numberOfColumns = rsMetaData.getColumnCount();
	//
	// Map<String, String> pojo;
	// List<Map<String, String>> pojoList = new LinkedList<Map<String,
	// String>>();
	// // put header
	// pojo = new TreeMap<String, String>();
	// pojo.put("000", "<td>STT");
	// for (int i = 1; i <= numberOfColumns; i++) {
	// pojo.put(String.format("%03d", i), "<td>" + rsMetaData.getColumnName(i));
	// }
	// pojoList.add(pojo);
	// int rowcount = 0;
	// while (rs != null && rs.next()) {
	// pojo = new TreeMap<String, String>();
	// rowcount = rowcount + 1;
	// pojo = new TreeMap<String, String>();
	// pojo.put("000", "<td>" + rowcount);
	// pojo.put("001", replaceNull("<td id = \"id\" >" + rs.getString(1)));
	// pojo.put("002", replaceNull("<td>" + rs.getString(2)));
	// pojo.put("003", replaceNull("<td>" + rs.getString(3)));
	// pojo.put("004", replaceNull("<td>" + rs.getString(4)));
	// pojo.put("005", replaceNull("<td id = \"end_date\" >" +
	// rs.getString(5)));
	// pojo.put("006",
	// replaceNull("<td class=\"canclick thc2\" align = \"right\" id = \"no_dk\"
	// >" + rs.getString(6)));
	// pojo.put("007",
	// replaceNull("<td class=\"canclick thc2\" align = \"right\" id =
	// \"kh_no_90\" >" + rs.getString(7)));
	// pojo.put("008", replaceNull(
	// "<td class=\"canclick thc2\" align = \"right\" id = \"kh_no_dong_n\" >" +
	// rs.getString(8)));
	// pojo.put("009", replaceNull(
	// "<td class=\"canclick thc2\" align = \"right\" id = \"kh_no_dong_n1\" >"
	// + rs.getString(9)));
	// pojo.put("010", replaceNull(
	// "<td class=\"canclick thc2\" align = \"right\" id = \"tien_nop_nh\" >" +
	// rs.getString(10)));
	// pojo.put("011",
	// replaceNull("<td class=\"canclick thc2\" align = \"right\" id =
	// \"kh_tttd\" >" + rs.getString(11)));
	// pojoList.add(pojo);
	//
	// }
	// return pojoList;
	// }

	@Override
	public List<Map<String, String>> updateAgentKH(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = new JSONArray(String.valueOf(jsonObject.get("updateparam")));

			conn = getConnection();
			conn.setAutoCommit(false);

			// String id, kh_90, kh_no_dong_n, kh_no_dong_n1, no_dk,
			// tien_nop_nh, kh_tttd, hscl, end_date = null;

			String id, kh_90, kh_no_dong_n, kh_no_dong_n1, no_dk, tien_nop_nh, end_date = "", kh_tttd;

			String id_where = "and a.collection_group_id in (";
			for (int i = 0; i < jsonArray.length(); i++) {
				prpStm = conn.prepareStatement("UPDATE hhtc_dl_thu_cuoc SET kh_90 = ?"
						+ " ,kh_no_dong_n = ?,kh_no_dong_n1 = ?,no_dk = ?,tien_nop_nh = ?,kh_tttd = ? "
						+ "  WHERE collection_group_id = ? AND end_date = TO_DATE(?, 'YYYY-MM-DD')");

				jsonObject = new JSONObject(jsonArray.get(i).toString());
				id = String.valueOf(jsonObject.get("id"));
				kh_90 = String.valueOf(jsonObject.get("kh_90"));
				kh_no_dong_n = String.valueOf(jsonObject.get("kh_no_dong_n"));
				kh_no_dong_n1 = String.valueOf(jsonObject.get("kh_no_dong_n1"));
				no_dk = String.valueOf(jsonObject.get("no_dk"));
				tien_nop_nh = String.valueOf(jsonObject.get("tien_nop_nh"));
				end_date = String.valueOf(jsonObject.get("end_date"));
				kh_tttd = String.valueOf(jsonObject.get("kh_tttd"));

				prpStm.setString(1, kh_90);
				prpStm.setString(2, kh_no_dong_n);
				prpStm.setString(3, kh_no_dong_n1);
				prpStm.setString(4, no_dk);
				prpStm.setString(5, tien_nop_nh);
				prpStm.setString(6, kh_tttd);
				prpStm.setString(7, id);
				prpStm.setString(8, end_date);
				prpStm.execute();
				id_where = id_where + id + " ,";
			}
			id_where = id_where + " -1)";
			conn.commit();
			prpStm.close();
			String sql = "SELECT '<td id = \"collection_group_id\" >'||a.collection_group_id id"
					+ "		,'<td>'||b.province province,'<td>'||b.name \"TÊN ÐẠI LÝ\""
					+ "		,'<td>'||TO_CHAR(a.sta_date, 'YYYY-MM-DD') \"NGÀY ÐẦU KỲ\""
					+ "		,'<td>'||TO_CHAR(a.end_date, 'YYYY-MM-DD') \"NGÀY CUỐI KỲ\""
					+ "		,'<td class=\"editable cnumber\" id = \"no_dk\" >'||no_dk \"NỢ ÐẦU KỲ\""
					+ "		,'<td class=\"editable cnumber\" id = \"kh_90\" >'||kh_90 \"KH NỢ 90\""
					+ "		,'<td class=\"editable cnumber\" id = \"kh_no_dong_n\" >'||kh_no_dong_n \"KH NỢ ÐỌNG NĂM N\""
					+ "		,'<td class=\"editable cnumber\" id = \"kh_no_dong_n1\" >'||kh_no_dong_n1 \"KH NỢ ÐỌNG N-1\""
					+ "		,'<td class=\"editable cnumber\" id = \"tien_nop_nh\" >'||tien_nop_nh \"TIỀN NỘP NGÂN HÀNG\""
					+ "		,'<td class=\"editable cnumber\" id = \"kh_tttd\" >'||kh_tttd \"KH THU TỰ ÐỘNG\""
					+ " FROM hhtc_dl_thu_cuoc a LEFT JOIN collection_group_v b "
					+ "  ON a.collection_group_id = b.collection_group_id "
					+ "  WHERE end_date = TO_DATE(?, 'YYYY-MM-DD') " + id_where + "  order by province, name";

			prpStm = conn.prepareStatement(sql);
			prpStm.setString(1, end_date);
			rs = prpStm.executeQuery();
			return getData2(rs, 0, user_name, "4");

		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> updateVungThuCuoc(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = new JSONArray(String.valueOf(jsonObject.get("updateparam")));

			conn = getConnection();
			conn.setAutoCommit(false);

			// String id, kh_90, kh_no_dong_n, kh_no_dong_n1, no_dk,
			// tien_nop_nh, kh_tttd, hscl, end_date = null;

			String pay_area_code = "", vung = "", end_date = "";

			String id_where = "and a.pay_area_code in (";
			for (int i = 0; i < jsonArray.length(); i++) {
				prpStm = conn.prepareStatement("UPDATE hhtc_pay_area SET vung = ?"
						+ "  WHERE pay_area_code = ? AND end_date = TO_DATE(?, 'YYYY-MM-DD')");

				jsonObject = new JSONObject(jsonArray.get(i).toString());
				pay_area_code = String.valueOf(jsonObject.get("id"));
				vung = String.valueOf(jsonObject.get("vung"));
				end_date = String.valueOf(jsonObject.get("end_date"));
				prpStm.setString(1, vung);
				prpStm.setString(2, pay_area_code);
				prpStm.setString(3, end_date);
				prpStm.execute();
				id_where = id_where + "'" + pay_area_code + "' ,";
			}
			id_where = id_where + " '-1')";
			conn.commit();
			prpStm.close();
			String sql = "SELECT '<td id = \"collection_group_id\" >'||a.pay_area_code \"MÃ VÙNG\""
					+ "     ,'<td>'||substr(pay_area_code,1,3) province"
					+ "     ,'<td id = \"end_date\">'||TO_CHAR(a.end_date, 'YYYY-MM-DD') \"NGÀY CUỐI KỲ\""
					+ "     ,'<td class=\"editable cnumber\" id = \"vung\" >'||vung \"VÙNG CƯỚC\""
					+ "     ,'<td class=\"cnumber\" id = \"ty_le_thu30\" >'||ty_le_thu30 \"TỶ LỆ THU 30\""
					+ "     ,'<td class=\"cnumber\" id = \"ty_le_no90\" >'||ty_le_no90 \"TỶ LỆ THU 90\""
					+ "     ,'<td class=\"cnumber\" id = \"sl_kh\" >'||sl_kh \"SỐ LƯỢNG KHÁCH HÀNG\""
					+ "  FROM        hhtc_pay_area a WHERE end_date = TO_DATE(?, 'YYYY-MM-DD') " + id_where
					+ " order by pay_area_code";

			prpStm = conn.prepareStatement(sql);
			prpStm.setString(1, end_date);
			rs = prpStm.executeQuery();
			return getData2(rs, 0, user_name, "4");

		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public Map<String, HoaHongThuCuoc> getHoaHongThuCuocList(String user_name, String json) {
		Map<String, HoaHongThuCuoc> hhtcMap = new TreeMap<String, HoaHongThuCuoc>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			JSONObject jsonObject = new JSONObject(json);
			String month, bill_cycle_id, v_end_date = "";
			String mbftinh = String.valueOf(jsonObject.get("mbftinh"));

			month = String.valueOf(jsonObject.get("month"));
			bill_cycle_id = String.valueOf(jsonObject.get("bill_cycle_id"));
			String sql = "	SELECT	 to_char(end_date,'YYYY-MM-DD') end_date FROM b4_bill_segment_v"
					+ "	 WHERE	 month = to_date(?,'YYYY-MM-DD') AND bill_cycle_id = ?";
			prpStm = conn.prepareStatement(sql);
			prpStm.setString(1, month + "-01");
			prpStm.setString(2, bill_cycle_id);
			syslog(month + ":" + bill_cycle_id);
			syslog(sql);
			rs = prpStm.executeQuery();
			while (rs != null && rs.next()) {
				v_end_date = rs.getString(1);
			}
			syslog("v_end_date :" + v_end_date);
			rs.close();
			prpStm.close();
			syslog("mbftinh :" + mbftinh);
			String sql_fix = "select collection_group_id, province, name, to_char(sta_date,'YYYY-MM-DD') sta_date"
					+ ", to_char(end_date,'YYYY-MM-DD') end_date, nvl(kh_90,0), nvl(th_90,0), nvl(hkh1,0), nvl(kh_no_dong_n,0)"
					+ ", nvl(th_no_dong_n,0), nvl(kh_no_dong_n1,0), nvl(th_no_dong_n1,0)"
					+ ", nvl(hkh2,0), nvl(no_dk,0), nvl(tm_thu_tk,0), nvl(tien_nop_nh,0), nvl(tien_dc_thanh_toan_cp,0)"
					+ ", nvl(no_ck,0), nvl(kh_tttd,0), nvl(th_tttd,0), hstd, hscl, dg_nds, dg_ndn, dg_ndn1,th_nds,th_nds*dg_nds thanh_tien_nds"
					+ ",nvl(kh_no_dong_n,0)+nvl(kh_no_dong_n1,0) kh_no_dong"
					+ ",nvl(th_no_dong_n,0)+nvl(th_no_dong_n1,0) th_no_dong,CHIPHI_TRONG_DS,CHIPHI_NGOAI_DS,CHIPHI_NODONG"
					+ ",nvl(tm_no_dong_n,0) tm_no_dong_n,nvl(tm_no_dong_n1,0) tm_no_dong_n1 from hhtc_dl_thu_cuoc_v"
					+ " where end_date=to_date(?,'YYYY-MM-DD')";
			if (mbftinh.equals("666666")) {
				sql = sql_fix;
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, v_end_date);
			} else {
				sql = sql_fix + " and province=get_province_code(?)";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, v_end_date);
				prpStm.setString(2, mbftinh);
			}
			syslog(sql);
			rs = prpStm.executeQuery();
			HoaHongThuCuoc hhtc;
			while (rs != null && rs.next()) {
				hhtc = new HoaHongThuCuoc();
				for (int i = 0; i < hhtc.paramArray.length; i++)
					hhtc.paramArray[i] = rs.getString(i + 1);
				hhtcMap.put(hhtc.paramArray[HoaHongThuCuoc.COLLECTION_GROUP_ID], hhtc);
			}
			rs.close();
			prpStm.close();

			sql = "SELECT a.collection_group_id,loai_tt,c.name ,trong_ds thu_duoc,duoc_huong,a.vung,muc_chi,a.ty_le_thu30,a.ty_le_no90,0 thanh_tien "
					+ "  FROM hhtc_trong_danh_sach a INNER JOIN hhtc_pay_area b"
					+ "		 ON a.end_date = to_date(?,'YYYY-MM-DD') and a.pay_area_code = b.pay_area_code AND a.end_date = b.end_date"
					+ "		 INNER JOIN pay_area_v c on a.pay_area_code=c.pay_area_code where loai_tt<>'other' order by a.vung,c.name,loai_tt";

			prpStm = conn.prepareStatement(sql);
			prpStm.setString(1, v_end_date);
			syslog(sql);
			rs = prpStm.executeQuery();
			HoaHongThuCuocTrongDs hhtc_trong_ds;
			while (rs != null && rs.next()) {
				if (hhtcMap.get(rs.getString(1)) != null) {
					hhtc_trong_ds = new HoaHongThuCuocTrongDs();
					for (int i = 0; i < hhtc_trong_ds.paramArray.length; i++)
						hhtc_trong_ds.paramArray[i] = rs.getString(i + 2);
					hhtcMap.get(rs.getString(1)).trongDs.add(hhtc_trong_ds);
				}
			}
			return hhtcMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> ChiTietThuCuoc(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String id, item, end_date;
			id = String.valueOf(jsonObject.get("id"));
			item = String.valueOf(jsonObject.get("item"));
			end_date = String.valueOf(jsonObject.get("end_date"));

			conn = getConnection();

			String sql = "";
			if (item.equals("th_90"))
				sql = "select type,cust_id,isdn,amount,des from hhtc_chi_tiet_thu_cuoc where des='debit_90' "
						// and TYPE <> 'other' "
						+ " and end_date = TO_DATE(?, 'YYYY-MM-DD') and collection_group_id = ?";
			else if (item.equals("th_no_dong_n"))
				sql = "select type,cust_id,isdn,amount,des,pay_staff_debit,pay_area_code from hhtc_chi_tiet_thu_cuoc where des='debit_n' "
						// and TYPE <> 'other' "
						+ " and end_date = TO_DATE(?, 'YYYY-MM-DD') and collection_group_id = ?";
			else if (item.equals("th_no_dong_n1"))
				sql = "select type,cust_id,isdn,amount,des,pay_staff_debit,pay_area_code from hhtc_chi_tiet_thu_cuoc where des IN ('debit_n1', 'debit_n2') "
						// and TYPE <> 'other' "
						+ " and end_date = TO_DATE(?, 'YYYY-MM-DD') and collection_group_id = ?";
			else if (item.equals("tm_thu_tk"))
				sql = "select type,cust_id,isdn,amount,des,pay_staff_debit,pay_area_code from hhtc_chi_tiet_thu_cuoc where des IN ('tm_trong_ky') "
						+ " and end_date = TO_DATE(?, 'YYYY-MM-DD') and collection_group_id = ?";
			else if (item.equals("thu_trong_ds"))
				sql = "select type,cust_id,isdn,CASE WHEN des IN ('debit_30', 'debit_60', 'debit_90') THEN NVL(amount, 0) "
						+ "ELSE 0 END  amount, CASE WHEN des = 'hot_all' AND TYPE <> 'TM' THEN 0 ELSE NVL(residual, 0) END hot"
						+ ",des,pay_staff_debit,pay_area_code from hhtc_chi_tiet_thu_cuoc "
						+ " where (TYPE = 'EZ' OR (TYPE = 'TM' AND pay_collection_group_id = collection_group_id)) "
						+ " and end_date = TO_DATE(?, 'YYYY-MM-DD') and collection_group_id = ?";
			else if (item.equals("th_nds"))
				sql = "select type,cust_id,isdn,amount,des,pay_staff_debit,pay_area_code from hhtc_chi_tiet_thu_cuoc "
						+ " WHERE   des = 'tm_trong_ky' AND cust_id NOT IN (SELECT	cust_id FROM hhtc_chi_tiet_thu_cuoc "
						+ " 		WHERE	des <> 'tm_trong_ky' and end_date = TO_DATE(?, 'YYYY-MM-DD') AND TYPE = 'TM') "
						+ " and end_date = TO_DATE(?, 'YYYY-MM-DD') and collection_group_id = ?";

			prpStm = conn.prepareStatement(sql);
			prpStm.setString(1, end_date);
			prpStm.setString(2, id);
			syslog(sql + " " + end_date + " " + id);
			if (item.equals("th_nds")) {
				prpStm.setString(1, end_date);
				prpStm.setString(2, end_date);
				prpStm.setString(3, id);
			}
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, "4");

		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public void addLog(String string) {
		Connection conn = null;
		PreparedStatement prpStm = null;
		try {
			conn = getConnection();
			prpStm = conn.prepareStatement("insert into qss_log(log_date_time , infor) VALUES(sysdate,?)");
			try {
				prpStm.setString(1, string.substring(0, 1000));
			} catch (Exception ex) {
				prpStm.setString(1, string);
			}
			prpStm.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			clean(conn, prpStm, null);
		}

	}

	@Override
	public List<Map<String, String>> getThongTinTraThuong(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			conn = getConnection();
			conn.setAutoCommit(false);
			buildIsdnTmp(jsonObject, conn, 0);
			String app = String.valueOf(jsonObject.get("app"));
			String type = String.valueOf(jsonObject.get("type"));
			String tu_ngay = String.valueOf(jsonObject.get("tu_ngay"));
			String den_ngay = String.valueOf(jsonObject.get("den_ngay"));
			String sql;
			if (type.equals("bl")) {
				sql = "begin retail_owner.CHECK_BL(TO_DATE(?, 'yyyy-mm-dd'), TO_DATE(?, 'yyyy-mm-dd')); end;";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, tu_ngay);
				prpStm.setString(2, den_ngay);
				prpStm.execute();
				prpStm.close();
				sql = "select * from retail_owner.CHECK_CTHH_BL_TMP";
				prpStm = conn.prepareStatement(sql);
			} else {
				sql = "begin retail_owner.CHECK_NAPTHE(TO_DATE(?, 'yyyy-mm-dd'), TO_DATE(?, 'yyyy-mm-dd')); end;";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, tu_ngay);
				prpStm.setString(2, den_ngay);
				prpStm.execute();
				prpStm.close();
				sql = "select * from retail_owner.CHECK_CTHH_NT_TMP";
				prpStm = conn.prepareStatement(sql);
			}
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> lncc_roimang(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			String isdn, donvi;
			JSONObject jsonObject = new JSONObject(json);

			String app = String.valueOf(jsonObject.get("app"));

			try {
				isdn = String.valueOf(jsonObject.get("isdn"));
			} catch (Exception e) {
				isdn = "";
			}
			try {
				donvi = String.valueOf(jsonObject.get("donvi"));
			} catch (Exception e) {
				donvi = "";
			}

			conn = getConnection();
			String sql;
			String sql_fix = "SELECT /*+DRIVING_SITE(b)*/ distinct '<td id = \"province\" >' || b.PROVINCE PROVINCE"
					+ ",'<td id = \"district\" >' || b.district district,"
					+ "'<td id = \"sub_id\" > <a target=\"_blank\" href=\"http://qlkh.mobifone.vn/1090/mobifone.jsp?txtSubID='||b.sub_id ||'&chkCheck=ACT&frmAction=SearchSub\">'||b.sub_id sub_id"
					+ ",'<td id = \"isdn\" >'||b.isdn isdn,'<td id = \"name\" >'||b.name name"
					+ ",'<td id = \"address\" >'||b.pay_full_address address"
					+ ",'<td id = \"last_act\" class = \"canclick\" >'||a.last_act last_act"
					+ ",'<td id = \"input_type\" >'||decode(a.input_type,1,'Chặn 2 chiều',2,'Chặn 1 chiều',3,'7 ngày ko psc',4) input_type"
					+ ",'<td id = \"status\" >' ||b.status status"
					+ ",'<td id = \"act_status\" >'||b.act_status act_status"
					+ ",'<td id = \"input_date\" >'||to_char(a.input_date,'YYYY-MM-DD') input_date"
					+ " FROM cskh_detai_sub_v a INNER JOIN (SELECT * FROM out_data.subscriber_v "
					+ " WHERE sub_id IN (SELECT sub_id FROM cskh_detai_sub WHERE close_date IS NULL)) b ON a.sub_id = b.sub_id";
			if (donvi.equals("666666")) {
				sql = sql_fix + " where close_date is null order by last_act,input_type,input_date";
				prpStm = conn.prepareStatement(sql);
			} else if (provinceNumberList.contains(donvi)) {
				sql = sql_fix
						+ " where close_date is null and b.province=get_province_code(?) order by last_act,input_type,input_date";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, donvi);
			} else if (isdn.equals("")) {
				sql = sql_fix + " where close_date is null and get_district_number(b.province||b.district) = ?"
						+ " order by  last_act,input_type,input_date";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, donvi);
			} else {
				sql = sql_fix + " where b.isdn = ?" + " order by last_act";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, isdn);
			}

			syslog(sql);
			rs = prpStm.executeQuery();
			return getData2(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> sub_care_history(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String sub_id = (String) jsonObject.get("sub_id");
			String input_date = (String) jsonObject.get("input_date");
			String app = String.valueOf(jsonObject.get("app"));
			conn = getConnection();
			String sql;
			sql = "select cskh_detai.get_act_label(act) act"
					+ " ,decode(act_status,'0','Đang thực hiện','1','Đã xong') act_status"
					+ " ,act_comment, act_time, user_name  from cskh_detai_sub_v"
					+ " where sub_id = ? and input_date >= to_date(?,'YYYY-MM-DD')"
					+ " and input_date < to_date(?,'YYYY-MM-DD')+1 and act_time is not null"
					+ " order by act_time desc";
			prpStm = conn.prepareStatement(sql);
			prpStm.setString(1, sub_id);
			prpStm.setString(2, input_date);
			prpStm.setString(3, input_date);
			syslog(sql);
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> add_care_history(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String sub_id = (String) jsonObject.get("sub_id");
			String input_date = (String) jsonObject.get("input_date");
			String act = (String) jsonObject.get("act");
			String status = (String) jsonObject.get("status");
			String comment = (String) jsonObject.get("comment");
			String app = String.valueOf(jsonObject.get("app"));
			String act_date = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
			conn = getConnection();
			String sql;

			/*
			 * sql = "select add_care_history(p_input_date DATE ,p_sub_id DATE
			 * ,p_act VARCHAR2 ,p_status VARCHAR2 ,p_comment VARCHAR2) from
			 * dual";"
			 */
			sql = "begin cskh_detai.add_care_history(?,to_date(?,'YYYY-MM-DD'),?,?,?,?,?); end;";
			prpStm = conn.prepareStatement(sql);
			prpStm.setString(1, sub_id);
			prpStm.setString(2, input_date);
			prpStm.setString(3, act);
			prpStm.setString(4, status);
			prpStm.setString(5, comment);
			prpStm.setString(6, act_date);
			prpStm.setString(7, user_name);
			prpStm.execute();
			prpStm.close();

			sql = "select '" + act_date + "' act_date, '" + user_name + "' user_name from dual";
			prpStm = conn.prepareStatement(sql);
			syslog(sql);
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> lncc_ketqua(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String from_date = String.valueOf(jsonObject.get("from_date")),
					to_date = String.valueOf(jsonObject.get("to_date")),
					donvi = String.valueOf(jsonObject.get("donvi"));
			String level = String.valueOf(jsonObject.get("level"));
			String app = String.valueOf(jsonObject.get("app"));
			conn = getConnection();
			prpStm = conn.prepareStatement(
					"begin set_mb6_program_ctx_pkg.setFromAndToDate(to_date(?,'YYYY-MM-DD'),to_date(?,'YYYY-MM-DD')) ; end;");
			prpStm.setString(1, from_date);
			prpStm.setString(2, to_date);
			prpStm.execute();
			prpStm.close();

			String sql;
			String sql_fix = "'<td id = \"sltb\" class = \"canclick cnumber\" >'||count (distinct sub_id||close_date) sltb"
					+ ",'<td id = \"sltb_het_nguy_co\" class = \"canclick cnumber\" >'||count (distinct case when close_date is not null and close_date < TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD')+1 then sub_id||close_date else null end) sltb_het_nguy_co"
					+ ",'<td id = \"sltb_binh_thuong\" class = \"canclick cnumber\" >'||count (distinct case when close_date is not null and close_date < TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD')+1 and close_status = 1 then sub_id||close_date else null end) sltb_binh_thuong"
					+ ",'<td id = \"sltb_bt_co_cham_soc\" class = \"canclick cnumber\" >'||count (distinct case when close_date is not null and close_date < TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD')+1 and close_status = 1 and act_time is not null and to_date(act_time,'YYYYMMDD-HH24MISS') >=TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'fromDate'), 'YYYYMMDD') and to_date(act_time,'YYYYMMDD-HH24MISS') < TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD')+1 and act <>'0' then sub_id||close_date else null end) sltb_bt_co_cham_soc"
					+ ",'<td id = \"co_hanh_dong\" class = \"canclick cnumber\" >'||count (distinct case when act_time is not null and to_date(act_time,'YYYYMMDD-HH24MISS') >=TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'fromDate'), 'YYYYMMDD') and to_date(act_time,'YYYYMMDD-HH24MISS') < TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD')+1 then sub_id||close_date else null end) co_hanh_dong"
					+ ",'<td id = \"co_hanh_dong_cham_soc\" class = \"canclick cnumber\" >'||count (distinct case when act_time is not null and to_date(act_time,'YYYYMMDD-HH24MISS') >=TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'fromDate'), 'YYYYMMDD') and to_date(act_time,'YYYYMMDD-HH24MISS') < TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD')+1 and act <>'0' then sub_id||close_date else null end) co_hanh_dong_cham_soc"
					+ ",'<td id = \"chua_co_hanh_dong\" class = \"canclick cnumber\" >'||count (distinct case when act_time is null or to_date(min_act_time,'YYYYMMDD-HH24MISS') > TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD')+1 then sub_id||close_date else null end) chua_co_hanh_dong "
					+ ",'<td id = \"can_co_hanh_dong\" class = \"canclick cnumber\" >'||count (distinct case when (close_date is null or close_date > TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD')+1) and (act_time is null or to_date(min_act_time,'YYYYMMDD-HH24MISS') > TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD')+1) then sub_id||close_date else null end) can_co_hanh_dong "
					+ " from cskh_detai_sub_v "
					+ " where input_date < TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD')  +1 and (close_date is null or close_date >= TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'fromDate'), 'YYYYMMDD'))";
			if (donvi.equals("666666")) {
				if (level.equals("0")) {
					sql = "select " + sql_fix;
				} else if (level.equals("1")) {
					sql = "select '<td id = \"province\">'|| province province," + sql_fix + " group by province";
				} else {
					sql = "select '<td id = \"province\">'|| province province"
							+ ",'<td id = \"district\">'|| district district," + sql_fix
							+ " GROUP BY   province,district";
				}
				prpStm = conn.prepareStatement(sql);

			} else if (provinceNumberList.contains(donvi)) {
				if (level.equals("0") || level.equals("1")) {
					sql = "select '<td id = \"province\">'|| province province," + sql_fix
							+ "	 AND province =get_province_code(?) group by province";
				} else {
					sql = "select '<td id = \"province\">'|| province province"
							+ ",'<td id = \"district\">'|| district district," + sql_fix
							+ "  AND province =get_province_code(?) GROUP BY   province,district";
				}
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, donvi);
			} else {
				sql = "select '<td id = \"province\">'|| province province"
						+ ",'<td id = \"district\">'|| district district," + sql_fix
						+ " AND get_district_number(province||district) = ? GROUP BY   province,district";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, donvi);
			}

			syslog(sql);
			rs = prpStm.executeQuery();
			return getData2(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> lncc_result_detail(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String from_date = String.valueOf(jsonObject.get("from_date")),
					to_date = String.valueOf(jsonObject.get("to_date")), area = String.valueOf(jsonObject.get("area")),
					column = String.valueOf(jsonObject.get("column"));
			String app = String.valueOf(jsonObject.get("app"));

			String area_clause = "", column_clause = "";

			if (area != null && !area.equals(""))
				area_clause = " and a.province||a.district like'" + area + "%'";
			else
				area_clause = "";
			conn = getConnection();
			String sql;

			prpStm = conn.prepareStatement(
					"begin set_mb6_program_ctx_pkg.setFromAndToDate(to_date(?,'YYYY-MM-DD'),to_date(?,'YYYY-MM-DD')) ; end;");
			prpStm.setString(1, from_date);
			prpStm.setString(2, to_date);
			prpStm.execute();
			prpStm.close();

			if (column.equals("sltb_het_nguy_co"))
				column_clause = " and close_date is not null and close_date < TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD')+1 ";
			else if (column.equals("sltb_binh_thuong"))
				column_clause = " and close_date is not null and close_date < TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD')+1 and close_status = 1";
			else if (column.equals("sltb_bt_co_cham_soc"))
				column_clause = " and close_date is not null and close_date < TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD')+1 and close_status = 1 and act_time is not null and to_date(act_time,'YYYYMMDD-HH24MISS') >=TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'fromDate'), 'YYYYMMDD') and to_date(act_time,'YYYYMMDD-HH24MISS') < TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD')+1 and act <>'0'";
			else if (column.equals("co_hanh_dong"))
				column_clause = " and act_time is not null and to_date(act_time,'YYYYMMDD-HH24MISS') >=TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'fromDate'), 'YYYYMMDD') and to_date(act_time,'YYYYMMDD-HH24MISS') < TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD')+1";
			else if (column.equals("co_hanh_dong_cham_soc"))
				column_clause = " and act_time is not null and to_date(act_time,'YYYYMMDD-HH24MISS') >=TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'fromDate'), 'YYYYMMDD') and to_date(act_time,'YYYYMMDD-HH24MISS') < TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD')+1 and act <>'0'";
			else if (column.equals("chua_co_hanh_dong"))
				column_clause = " and  act_time is null or to_date(min_act_time,'YYYYMMDD-HH24MISS') > TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD') +1";
			else if (column.equals("can_co_hanh_dong"))
				column_clause = " and (close_date is null or close_date > TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD')+1) and (act_time is null or to_date(min_act_time,'YYYYMMDD-HH24MISS') > TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD')+1)";

			sql = "select distinct b.PROVINCE, b.DISTRICT, b.ISDN, b.SUB_ID, decode(a.input_type,1,'Chặn 2 chiều',2,'Chặn 1 chiều',3,'7 ngày ko psc',4) INPUT_TYPE, a.INPUT_DATE "
					+ ", a.CLOSE_STATUS, a.CLOSE_DATE,a.last_act" + " FROM cskh_detai_sub_v a INNER JOIN  (SELECT *"
					+ "  FROM out_data.subscriber_v" + "  WHERE sub_id IN" + "        (SELECT sub_id"
					+ " FROM cskh_detai_sub" + " WHERE input_date <" + "   TO_DATE("
					+ "       SYS_CONTEXT('mb6_program_ctx', 'toDate')" + "      ,'YYYYMMDD')" + "   + 1"
					+ "   AND (close_date IS NULL" + "     OR close_date >=" + "    TO_DATE("
					+ "        SYS_CONTEXT('mb6_program_ctx'" + "   ,'fromDate')"
					+ "       ,'YYYYMMDD')))) b ON a.sub_id = b.sub_id"
					+ " where input_date < TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'toDate'), 'YYYYMMDD') +1 and (close_date is null or close_date >= TO_DATE(SYS_CONTEXT('mb6_program_ctx', 'fromDate'), 'YYYYMMDD')) "
					+ column_clause + area_clause;
			prpStm = conn.prepareStatement(sql);
			syslog(sql);
			rs = prpStm.executeQuery();
			return getData(rs, 1, user_name, app);

		} catch (

		Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> getLnccActList(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		JSONObject jsonObject = new JSONObject(json);
		String app = String.valueOf(jsonObject.get("app")), act_group = String.valueOf(jsonObject.get("act_group"));
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			String sql = "select act_id,label from cskh_detai_act a where instr(?,act_group) >= 1 "
					+ " and (role_list IS NULL OR EXISTS (SELECT 1 FROM account_action_v"
					+ "  WHERE name = ? AND INSTR(a.role_list, role) >= 1))";

			syslog(sql);
			prpStm = conn.prepareStatement(sql);
			prpStm.setString(1, act_group);
			prpStm.setString(2, user_name);
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> mobitv_roimang(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			String sub_id, donvi;
			JSONObject jsonObject = new JSONObject(json);

			String app = String.valueOf(jsonObject.get("app"));
			String type = String.valueOf(jsonObject.get("type"));
			conn = getConnection();
			String sql;
			String sql_fix = "SELECT distinct '<td id = \"province\" >' || PROVINCE PROVINCE"
					+ ",'<td id = \"district\" >' || DISTRICT district,'<td id = \"sub_id\" >'||sub_id sub_id"
					+ ",'<td id = \"isdn\" >'||PHONE isdn,'<td id = \"name\" >'||name name"
					+ ",'<td id = \"SUB_TYPE\" >'||SUB_TYPE SUB_TYPE,'<td id = \"address\" >'||address address"
					+ ",'<td id = \"last_act\" class = \"canclick\" >'||last_act last_act"
					+ ",'<td id = \"SERVICE_PKG\" >'||SERVICE_PKG SERVICE_PKG"
					+ ",'<td id = \"dlc1_reg\" >'||dlc1_reg dlc1_reg" + ",'<td id = \"unit_reg\" >'||unit_reg unit_reg"
					+ ",'<td id = \"contract_date\" >'||TO_CHAR(contract_date, 'YYYY-MM-DD') contract_date"
					+ ",'<td id = \"ACTIVE_DATE\" >'||TO_CHAR(active_date, 'YYYY-MM-DD') ACTIVE_DATE"
					+ ",'<td id = \"EXPIRE_DATE\" >'||TO_CHAR(EXPIRE_DATE, 'YYYY-MM-DD') EXPIRE_DATE"
					+ " FROM mobitv_v a";
			if (type.equals("expire")) {
				donvi = String.valueOf(jsonObject.get("donvi"));
				if (donvi.equals("666666")) {
					sql = sql_fix
							+ " WHERE expire_date >= trunc(SYSDATE) AND expire_date < trunc(SYSDATE) + 5 ORDER BY last_act,expire_date";
					prpStm = conn.prepareStatement(sql);
				} else if (provinceNumberList.contains(donvi)) {
					sql = sql_fix
							+ " WHERE expire_date >= trunc(SYSDATE) AND expire_date < trunc(SYSDATE) + 5 and province=get_province_code(?) order by last_act,EXPIRE_DATE";
					prpStm = conn.prepareStatement(sql);
					prpStm.setString(1, donvi);
				} else {
					sql = sql_fix
							+ " WHERE expire_date >= trunc(SYSDATE) AND expire_date < trunc(SYSDATE) + 5 and get_district_number(province||district) = ? order by last_act,EXPIRE_DATE";
					prpStm = conn.prepareStatement(sql);
					prpStm.setString(1, donvi);

				}
			} else if (type.equals("find_sub")) {
				sub_id = String.valueOf(jsonObject.get("sub_id"));
				sql = sql_fix + " WHERE sub_id = ?";
				prpStm = conn.prepareStatement(sql);
				prpStm.setString(1, sub_id);

			} else {
				donvi = String.valueOf(jsonObject.get("donvi"));
				if (donvi.equals("666666")) {
					sql = sql_fix + " WHERE contract_status is null AND NOT EXISTS "
							+ "(SELECT 1 FROM mobitv_v WHERE sub_id = a.sub_id AND act = 6 AND act_status = 1)";
					prpStm = conn.prepareStatement(sql);
				} else if (provinceNumberList.contains(donvi)) {
					sql = sql_fix + "  WHERE contract_status is null AND NOT EXISTS "
							+ "(SELECT 1 FROM mobitv_v WHERE sub_id = a.sub_id AND act = 6 AND act_status = 1)"
							+ " and province=get_province_code(?) order by last_act";
					prpStm = conn.prepareStatement(sql);
					prpStm.setString(1, donvi);
				} else {
					sql = sql_fix + "  WHERE contract_status is null AND NOT EXISTS "
							+ "(SELECT 1 FROM mobitv_v WHERE sub_id = a.sub_id AND act = 6 AND act_status = 1)"
							+ " and get_district_number(province||district) = ? order by last_act";
					prpStm = conn.prepareStatement(sql);
					prpStm.setString(1, donvi);
				}
			}

			syslog(sql);
			rs = prpStm.executeQuery();
			return getData2(rs, 1, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> mobitv_care_history(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String sub_id = (String) jsonObject.get("sub_id");
			String app = String.valueOf(jsonObject.get("app"));
			conn = getConnection();
			String sql;
			sql = "select cskh_detai.get_act_label(act) act"
					+ " ,decode(act_status,'0','Đang thực hiện','1','Đã xong') act_status"
					+ " ,act_comment, act_time, user_name  from mobitv_care_history_v"
					+ " where sub_id = ? and act_time is not null order by act_time desc";
			prpStm = conn.prepareStatement(sql);
			prpStm.setString(1, sub_id);
			syslog(sql);
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> add_mobitv_care_history(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			String sub_id = (String) jsonObject.get("sub_id");
			String act = (String) jsonObject.get("act");
			String status = (String) jsonObject.get("status");
			String comment = (String) jsonObject.get("comment");
			String app = String.valueOf(jsonObject.get("app"));
			String act_date = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
			conn = getConnection();
			String sql;

			/*
			 * sql = "select add_care_history(p_input_date DATE ,p_sub_id DATE
			 * ,p_act VARCHAR2 ,p_status VARCHAR2 ,p_comment VARCHAR2) from
			 * dual";"
			 */
			sql = "begin cskh_detai.add_care_history_mobitv(?,?,?,?,?,?); end;";
			prpStm = conn.prepareStatement(sql);
			prpStm.setString(1, sub_id);
			prpStm.setString(2, act);
			prpStm.setString(3, status);
			prpStm.setString(4, comment);
			prpStm.setString(5, act_date);
			prpStm.setString(6, user_name);
			prpStm.execute();
			prpStm.close();

			sql = "select '" + act_date + "' act_date, '" + user_name + "' user_name from dual";
			prpStm = conn.prepareStatement(sql);
			syslog(sql);
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> mobitv_ketqua(String user_name, String json) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, String>> chiTietCell(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {

			JSONObject jsonObject = new JSONObject(json);

			String app = String.valueOf(jsonObject.get("app"));
			String tu_ngay = String.valueOf(jsonObject.get("tu_ngay"));
			String den_ngay = String.valueOf(jsonObject.get("den_ngay"));
			String donvi = String.valueOf(jsonObject.get("donvi"));
			String rptype = String.valueOf(jsonObject.get("rptype"));
			String level = String.valueOf(jsonObject.get("level"));
			conn = getConnection();
			String sql = "BEGIN set_mb6_program_ctx_pkg.setfromandtodate(TO_DATE(?, 'YYYY-MM-DD'), TO_DATE(?, 'YYYY-MM-DD')); END;";
			prpStm = conn.prepareStatement(sql);
			prpStm.setString(1, tu_ngay);
			prpStm.setString(2, den_ngay);
			prpStm.execute();

			String sql_fix = ",count(distinct site_name) so_tram ,count(distinct CELL_ID) so_cell"
					+ ",round(SUM(VLR)/count(distinct ngay_tong_hop)) VLR"
					+ ",round(SUM(ZP)/count(distinct ngay_tong_hop)) ZP"
					+ ",round(SUM(MQ)/count(distinct ngay_tong_hop)) MQ"
					+ ",round(SUM(MC)/count(distinct ngay_tong_hop)) MC"
					+ ",round(SUM(QSV)/count(distinct ngay_tong_hop)) QSV"
					+ ",round(SUM(FC)/count(distinct ngay_tong_hop)) FC"
					+ ",round(SUM(OTHER)/count(distinct ngay_tong_hop)) TT_OTHER"
					+ ",round(SUM(TS)/count(distinct ngay_tong_hop)) TS"
					+ ",round(SUM(TOTAL_BONUS)/count(distinct ngay_tong_hop)) TOTAL_BONUS"
					+ ",round(SUM(TOTAL_CREDIT)/count(distinct ngay_tong_hop)) TOTAL_CREDIT"
					+ ",round(SUM(THOAI)/count(distinct ngay_tong_hop)) THOAI"
					+ ",round(SUM(DATA)/count(distinct ngay_tong_hop)) DATA"
					+ ",round(SUM(GTGT)/count(distinct ngay_tong_hop)) GTGT"
					+ ",round(SUM(SMS)/count(distinct ngay_tong_hop)) SMS"
					+ ",round(SUM(RMQT)/count(distinct ngay_tong_hop)) RMQT"
					+ ",round(SUM(KHAC)/count(distinct ngay_tong_hop)) KHAC"
					+ ",round(SUM(MULTIMEDIA)/count(distinct ngay_tong_hop)) MULTIMEDIA"
					+ ",round(SUM(THOAI_B)/count(distinct ngay_tong_hop)) THOAI_B"
					+ ",round(SUM(DATA_B)/count(distinct ngay_tong_hop)) DATA_B"
					+ ",round(SUM(GTGT_B)/count(distinct ngay_tong_hop)) GTGT_B"
					+ ",round(SUM(SMS_B)/count(distinct ngay_tong_hop)) SMS_B"
					+ ",round(SUM(RMQT_B)/count(distinct ngay_tong_hop)) RMQT_B"
					+ ",round(SUM(KHAC_B)/count(distinct ngay_tong_hop)) KHAC_B"
					+ ",round(SUM(MULTIMEDIA_B)/count(distinct ngay_tong_hop)) MULTIMEDIA_B"
					+ ",round(SUM(VOLUMN_DATA)/count(distinct ngay_tong_hop),2) VOLUMN_DATA"
					+ ",round(SUM(VOLUMN_SMS)/count(distinct ngay_tong_hop)) VOLUMN_SMS"
					+ ",round(SUM(VOLUMN_VOICE)/count(distinct ngay_tong_hop)) VOLUMN_VOICE        "
					+ " FROM RPT_CELLDATA_V";
			if (rptype.equals("0"))
				if (donvi.equals("666666")) {
					sql = "SELECT * from RPT_CELLDATA_V ORDER BY site_name,cell_name,ngay_tong_hop";
					prpStm = conn.prepareStatement(sql);
				} else if (provinceNumberList.contains(donvi)) {
					sql = "SELECT * from RPT_CELLDATA_V WHERE province=get_province_code(?)"
							+ " ORDER BY site_name,cell_name,ngay_tong_hop";
					prpStm = conn.prepareStatement(sql);
					prpStm.setString(1, donvi);
				} else {
					sql = "SELECT * from RPT_CELLDATA_V WHERE get_district_number(province||district) = ?"
							+ " ORDER BY site_name,cell_name,ngay_tong_hop";
					prpStm = conn.prepareStatement(sql);
					prpStm.setString(1, donvi);
				}
			else if (rptype.equals("1")) {
				if (donvi.equals("666666")) {
					sql = "SELECT SITE_NAME,ngay_phat_song, province,district ,loai_cell,level_time" + sql_fix
							+ " GROUP BY SITE_NAME,ngay_phat_song,province,district,loai_cell,level_time ORDER BY site_name";
					prpStm = conn.prepareStatement(sql);
				} else if (provinceNumberList.contains(donvi)) {
					sql = "SELECT SITE_NAME,ngay_phat_song, province,district ,loai_cell,level_time" + sql_fix
							+ " where province=get_province_code(?)"
							+ " GROUP BY SITE_NAME,ngay_phat_song,province,district,loai_cell,level_time ORDER BY site_name";
					prpStm = conn.prepareStatement(sql);
					prpStm.setString(1, donvi);
				} else {
					sql = "SELECT SITE_NAME,ngay_phat_song, province,district ,loai_cell,level_time" + sql_fix
							+ " where get_district_number(province||district) = ?"
							+ " GROUP BY SITE_NAME,ngay_phat_song,province,district ,loai_cell,level_time ORDER BY site_name";
					prpStm = conn.prepareStatement(sql);
					prpStm.setString(1, donvi);
				}
			} else {
				if (donvi.equals("666666")) {
					if (level.equals("0")) {
						sql = "SELECT loai_cell,level_time" + sql_fix
								+ " GROUP BY loai_cell,level_time ORDER BY loai_cell,level_time";

					} else if (level.equals("1")) {
						sql = "SELECT province,loai_cell,level_time" + sql_fix
								+ " GROUP BY province,loai_cell,level_time ORDER BY province,loai_cell,level_time";

					} else {
						sql = "SELECT province,district ,loai_cell,level_time" + sql_fix
								+ " GROUP BY province,district,loai_cell,level_time ORDER BY province,district,loai_cell,level_time";

					}
					prpStm = conn.prepareStatement(sql);
				} else if (provinceNumberList.contains(donvi)) {
					if (level.equals("0") || level.equals("1")) {
						sql = "SELECT province,loai_cell,level_time" + sql_fix + " where province=get_province_code(?) "
								+ " GROUP BY province,loai_cell,level_time ORDER BY province,loai_cell,level_time";
					} else {
						sql = "SELECT province,district ,loai_cell,level_time" + sql_fix
								+ " where province=get_province_code(?) "
								+ " GROUP BY province,district,loai_cell,level_time ORDER BY province,district,loai_cell,level_time";
					}
					prpStm = conn.prepareStatement(sql);
					prpStm.setString(1, donvi);
				} else {
					sql = "SELECT province,district ,loai_cell,level_time" + sql_fix
							+ " where get_district_number(province||district) = ? "
							+ " GROUP BY province,district,loai_cell,level_time ORDER BY province,district,loai_cell,level_time";
					prpStm = conn.prepareStatement(sql);
					prpStm.setString(1, donvi);
				}

			}
			syslog(sql);
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (

		Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	public Workbook exportDataExcel(String sqlParam, String[] sqlArray, File fileTemplate, long[] totRec)
			throws Exception {
		Connection conn = getConnection();
		try {
			return Util.exportDataExcel(sqlParam, sqlArray, fileTemplate, conn, totRec);
		} finally {
			conn.close();
		}

	}

	@Override
	public Object getVlr3k3dLatday() {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		String vlr3k3dLatday = "";
		try {

			conn = getConnection();
			String sql = "select substr(file_name,-15,8) from (select * from out_data.process_file_history_v"
					+ " where file_name like '3k3d_vlr_lk%'" + " order by process_date desc) where rownum=1";
			prpStm = conn.prepareStatement(sql);
			syslog(sql);
			rs = prpStm.executeQuery();
			while (rs != null && rs.next()) {
				vlr3k3dLatday = rs.getString(1);
			}
			return vlr3k3dLatday;
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> get_auto_report(String user_name, String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			String configpath = String.valueOf(jsonObject.get("configpath"));
			template_dir = new File(configpath + File.separator + "template");
			syslog(template_dir.getAbsolutePath());
			return ThreadManager.listNode(configpath);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> add_modify_rp(String user_name, String json, MultipartHttpServletRequest request) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			String configpath = String.valueOf(jsonObject.get("configpath"));
			// String app = String.valueOf(jsonObject.get("app"));
			// String file_name = String.valueOf(jsonObject.get("file_name"));
			// CommonsMultipartFile formData = CommonsMultipartFile.
			// jsonObject.get("formData");
			JSONArray jsonArray = new JSONArray(String.valueOf(jsonObject.get("updateparam")));
			Iterator<String> iterator = request.getFileNames();
			MultipartFile file = null;
			File[] fileAray = new File[jsonArray.length()];
			File file2 = null;
			// template_dir = new File(configpath + File.separator +
			// "template");
			if (!template_dir.exists())
				template_dir.mkdirs();
			while (iterator.hasNext()) {
				file = request.getFile(iterator.next());
				syslog(file.getName().substring(0, 4));
				file2 = new File(template_dir, file.getName().substring(4));
				fileAray[Integer.parseInt(file.getName().substring(0, 4)) - 1] = file2;
				file.transferTo(file2);
				// do something with the file.....
			}

			String id, org_id;

			Dictionary dicThreadList = ManageableThread
					.loadThreadConfig(configpath + File.separator + ThreadConstant.THREAD_CONFIG_FILE);
			Dictionary dictionaryNew = new Dictionary();

			@SuppressWarnings("rawtypes")
			Vector vtThreadList = dicThreadList.mndRoot.mvtChild;

			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = new JSONObject(jsonArray.get(i).toString());
				id = String.valueOf(jsonObject.get("id"));
				org_id = String.valueOf(jsonObject.get("org_id"));
				for (int iIndex = 0; iIndex < vtThreadList.size(); iIndex++) {
					// Get thread info
					DictionaryNode node = (DictionaryNode) vtThreadList.elementAt(iIndex);

					String strThreadID = node.mstrName;
					if (strThreadID.equals(org_id)) {
						DictionaryNode newNode = node.clone();
						newNode.mstrName = String.valueOf(i + 1);
						if (id.equals("0"))
							newNode.setChildValue("ThreadName", newNode.mstrName);
						if (fileAray[i] != null)
							newNode.getChild("Parameter").setChildValue("TemplatePath", fileAray[i].getAbsolutePath());
						newNode.setChildValue("StartupType", "2");
						dictionaryNew.mndRoot.mvtChild.add(newNode);
					}

				}
			}

			ManageableThread.storeThreadConfig(dictionaryNew,
					configpath + File.separator + ThreadConstant.THREAD_CONFIG_FILE);
			return ThreadManager.listNode(configpath);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@Override
	public File getTemplateDir() {
		// TODO Auto-generated method stub
		return template_dir;
	}

	@Override
	public List<GmapMarker> getCellGmapMarkers(String user_name) {
		Connection conn;
		try {
			conn = getConnection();
			Statement sttm = conn.createStatement();
			String sql = "select distinct replace(cell_type ||' '||to_char(ngay_phat_song,'YYYYMMDD') || ' ' ||site_name||' '||address_detail,'\\','') description"
					+ ",province,district,replace(replace(latitude,',','.')||',' ||replace(longitude,',','.'),'..','.') googeladdress"
					+ ",case when sysdate - ngay_phat_song <60 then 1 else 0 end new_member"
					+ " from out_data.cell_infor_v where province in ('THO','NAN','HTI','QBI')"
					+ " and cell_id in (select distinct  '452-01-'||cell_id from OUT_DATA.luuluong_cell"
					+ " where sum_date >= trunc(sysdate-10))";
			ResultSet rs = sttm.executeQuery(sql);
			/*
			 * ResultSet rs = sttm.executeQuery(
			 * "SELECT tinh_thanh_pho id_tinh,quan_huyen id_huyen,id,ten_cua_hang,dia_chi,so_ez_nhan_tien,vi_do ||',' || kinh_do googeladdress"
			 * +
			 * "  FROM out_data.diem_ban_hang_msale_v where tinh_thanh_pho in (370,380,390,520)"
			 * +
			 * "  and nvl(so_ez_nhan_tien,'0')<>'0' and is_active=1 and vi_do is not null and kinh_do is not null"
			 * );
			 */
			List<GmapMarker> pojoList = new LinkedList<GmapMarker>();
			String app = "2";
			GmapMarker pojo;
			if (filterBuiledMap.get(user_name + ".." + app) == null)
				buildFilter(user_name, app);
			Mb6Fillter mb6Fillter = filterBuiledMap.get(user_name + ".." + app);

			while (rs != null && rs.next()) {
				if (mb6Fillter.isAllProvince() || mb6Fillter.getProvinceList().contains("" + rs.getString("province"))
						&& (mb6Fillter.isAllDistrict()
								|| mb6Fillter.getDistrictList().contains("" + rs.getString("district")))) {
					pojo = new GmapMarker();
					pojo.setGoogelAddress(rs.getString("googeladdress"));
					pojo.setNew_member(rs.getString("new_member"));
					pojo.setDescription(rs.getString("description"));
					pojoList.add(pojo);
				}
			}
			// TODO Auto-generated method stub
			conn.close();
			return pojoList;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Map<String, String>> getStockSimDetail(String user_name, String json) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			conn = getConnection();
			String separator = ";";
			String isdn_list = String.valueOf(jsonObject.get("isdn_list")).replaceAll("[^\\d.]", separator);
			String aliving_date;
			try {
				aliving_date = String.valueOf(jsonObject.get("aliving_date"));
				if (aliving_date == null || aliving_date.equals(""))
					aliving_date = "4000-01-01";

			} catch (Exception e) {
				aliving_date = "4000-01-01";
			}
			String[] subArray = isdn_list.split(separator);

			if (aliving_date == null || aliving_date.equals(""))
				aliving_date = "4000-01-01";

			int k = subArray.length / 15000;
			int d = subArray.length % 15000;
			prpStm = conn.prepareStatement("BEGIN delete sub_act_temp; END;");
			prpStm.execute();

			for (int j = 0; j < k; j++) {
				prpStm = conn.prepareStatement("BEGIN INSERT INTO sub_act_temp(isdn) "
						+ "VALUES (?);EXCEPTION WHEN OTHERS THEN	NULL; END;");

				for (int i = j * 15000; i < j * 15000 + 14999; i++) {
					try {
						prpStm.setString(1, subArray[i]);

						prpStm.addBatch();
					} catch (Exception e) {
						syslog(subArray[i] + " Loi o so nay " + e.toString());
					}
				}
				prpStm.executeBatch();
				prpStm.close();
			}

			prpStm = conn.prepareStatement(
					"BEGIN INSERT INTO sub_act_temp(isdn) " + "VALUES (?);EXCEPTION WHEN OTHERS THEN	NULL; END;");

			for (int i = k * 15000; i < k * 15000 + d; i++) {
				try {
					prpStm.setString(1, subArray[i]);
					prpStm.addBatch();
				} catch (Exception e) {
					syslog(subArray[i] + " Loi o so nay " + e.toString());
				}
			}
			prpStm.executeBatch();
			prpStm.close();

			String app = String.valueOf(jsonObject.get("app"));
			String sql = "WITH seri_sim AS (SELECT * FROM out_data.stock_sim_v"
					+ " WHERE serial IN (SELECT isdn FROM sub_act_temp))"
					+ " SELECT a.isdn serial,c.auc_status,nvl(b.hlr_isdn,d.isdn) isdn,b.hlr_status"
					+ " ,b.center_code,nvl(b.active_datetime,d.sta_datetime)  active_datetime"
					+ " ,case when b.hlr_isdn is not null then 'TT' when d.isdn is not null then 'TS' else null end Loai_TB"
					+ " FROM sub_act_temp a LEFT JOIN  seri_sim c ON a.isdn = c.serial"
					+ "     LEFT JOIN out_data.mc_subscriber_v b ON c.imsi = b.hlr_imsi AND b.hlr_status = 1"
					+ "     LEFT JOIN out_data.subscriber_v d ON c.imsi = d.imsi AND d.status = 1 ORDER BY serial";
			prpStm = conn.prepareStatement(sql);
			rs = prpStm.executeQuery();
			return getData(rs, 0, user_name, app);
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Override
	public List<Map<String, String>> importFromUploadFile(String user_name, String json,
			MultipartHttpServletRequest request) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			JSONObject jsonObject = new JSONObject(json);

			// String app = String.valueOf(jsonObject.get("app"));
			String filetype = "";
			Iterator<String> iterator = request.getFileNames();
			MultipartFile file = null;

			File file2 = null;
			// template_dir = new File(configpath + File.separator +
			// "template");
			while (iterator.hasNext()) {
				file = request.getFile(iterator.next());
				filetype = file.getName();
				syslog(file.getName());
				file2 = new File(temp_dir, request.getRequestedSessionId() + ".xls");
				file.transferTo(file2);
			}

			BufferedInputStream bi = new BufferedInputStream(new FileInputStream(file2));
			Workbook w = new HSSFWorkbook(bi);

			// w = Workbook .getWorkbook(file2);
			// Get the first sheet
			Sheet sheet = w.getSheetAt(0);
			String sql = "";
			conn = getConnection();
			Row row;
			Map<String, String> pojo;
			List<Map<String, String>> pojoList = new LinkedList<Map<String, String>>();

			if (filetype.equals("reg_file")) {
				pojo = new TreeMap<String, String>();
				// put header
				String[] header = { "Mã thuê bao", "Tên KH", "Địa chỉ", "Xã Phường", "Quận/Huyện", "Tỉnh/Thành",
						"Ngày kích hoạt", "Mã chương trình", "Loại STB", "Số chíp", "Số thẻ", "Mã kho", "Tên kho",
						"Kho MBF DV", "Kho MBF CN", "Kho MBF C1", "Số đthoại kích hoạt", "MBF KV kích hoạt",
						"MBF CN kích hoạt", "DLC1 kích  hoạt", "Tên đơn vị kích hoạt", "Loại Đơn vị" };
				String[] header_source = new String[header.length];
				row = sheet.getRow(2);
				for (int j = 0; j <= 22; j++) {
					if (j <= 2) {
						header_source[j] = row.getCell(j).getStringCellValue();
					} else if (j >= 4) {
						header_source[j - 1] = row.getCell(j).getStringCellValue();
					}
				}
				for (int j = 0; j < header.length; j++) {
					if (!header_source[j].equals(header[j]))
						throw new Exception("Header không khớp");
				}

				pojo.put("000", "<th>ROW");

				for (int j = 0; j < 22; j++)
					pojo.put(String.format("%03d", j + 1), "<th>" + header[j]);
				pojo.put(String.format("%03d", 23), "<th>ERROR");
				pojoList.add(pojo);

				String[] headerArray = "SUB_ID,NAME,ADDRESS,PRECINCT,DISTRICT,PROVINCE,ACTIVE_DATE,PROGRAM_CODE,DEVICE_TYPE,IC_NUMBER,CARD_NUMBER,STOCK_CODE,STOCK_NAME,STOCK_MBF_DV,STOCK_MBF_CN,STOCK_MBF_C1,PHONE,MBF_KV_REG,MBF_CN_REG,DLC1_REG,UNIT_REG,UNIT_REG_TYPE"
						.split(",");
				String insertField = "SUB_ID,NAME,ADDRESS,PRECINCT,DISTRICT,PROVINCE,ACTIVE_DATE,PROGRAM_CODE,DEVICE_TYPE,IC_NUMBER,CARD_NUMBER,STOCK_CODE,STOCK_NAME,STOCK_MBF_DV,STOCK_MBF_CN,STOCK_MBF_C1,PHONE,MBF_KV_REG,MBF_CN_REG,DLC1_REG,UNIT_REG,UNIT_REG_TYPE,SUB_ID,NAME,ADDRESS,PRECINCT,DISTRICT,PROVINCE,ACTIVE_DATE,PROGRAM_CODE,DEVICE_TYPE,IC_NUMBER,CARD_NUMBER,STOCK_CODE,STOCK_NAME,STOCK_MBF_DV,STOCK_MBF_CN,STOCK_MBF_C1,PHONE,MBF_KV_REG,MBF_CN_REG,DLC1_REG,UNIT_REG,UNIT_REG_TYPE,ACTIVE_DATE";
				String[] bindInsertHeaderArray = insertField.split(",");
				int[] locationValue = new int[bindInsertHeaderArray.length];

				for (int j = 0; j < bindInsertHeaderArray.length; j++) {
					for (int i = 0; i < headerArray.length; i++) {
						if (bindInsertHeaderArray[j].equals(headerArray[i])) {
							locationValue[j] = i;
						}
					}
				}

				String[] valueArray = new String[headerArray.length];

				sql = "MERGE INTO mobitv USING DUAL ON (sub_id = ?) WHEN MATCHED THEN"
						+ "    UPDATE SET name = ?, address = ?, precinct = ?, district = ?,"
						+ "               province = ?, active_date = to_date(?,'yyyy-MM-dd'), program_code = ?,"
						+ "               device_type = ?, ic_number = ?, card_number = ?,"
						+ "               stock_code = ?, stock_name = ?, stock_mbf_dv = ?,"
						+ "               stock_mbf_cn = ?, stock_mbf_c1 = ?, phone = ?,"
						+ "               mbf_kv_reg = ?, mbf_cn_reg = ?, dlc1_reg = ?,"
						+ "               unit_reg = ?, unit_reg_type = ?,apply_date=sysdate,apply_user = '" + user_name
						+ "' WHEN NOT MATCHED THEN"
						+ "    INSERT(sub_id, name, address, precinct,district, province, active_date,"
						+ "           program_code, device_type, ic_number, card_number, stock_code,"
						+ "           stock_name, stock_mbf_dv, stock_mbf_cn, stock_mbf_c1, phone,"
						+ "           mbf_kv_reg, mbf_cn_reg, dlc1_reg, unit_reg, unit_reg_type,contract_date,apply_date,apply_user)"
						+ "    VALUES (?,?,?,?,?,?,to_date(?,'yyyy-MM-dd'),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,to_date(?,'yyyy-MM-dd'),sysdate,'"
						+ user_name + "')";
				prpStm = conn.prepareStatement(sql);
				for (int i = 3; i < sheet.getLastRowNum(); i++) {
					try {
						row = sheet.getRow(i);
						for (int j = 0; j <= 22; j++) {
							if (j <= 2) {
								valueArray[j] = row.getCell(j).getStringCellValue();
								prpStm.setString(j + 1, row.getCell(j).getStringCellValue());
							} else if (j >= 4) {
								if (j == 7) {
									valueArray[j - 1] = AppParam.YYYY_MM_FORMAT
											.format(row.getCell(j).getDateCellValue().getTime());
								} else {
									valueArray[j - 1] = row.getCell(j).getStringCellValue();
								}
							}
						}
						for (int j = 0; j < bindInsertHeaderArray.length; j++) {
							prpStm.setString(j + 1, valueArray[locationValue[j]]);
						}
						prpStm.execute();
						conn.commit();
					} catch (Exception e) {
						pojo = new TreeMap<String, String>();
						pojo.put("000", "<td>" + i);
						for (int j = 0; j <= 22; j++) {
							if (j <= 2)
								pojo.put(String.format("%03d", j + 1), "<td>" + row.getCell(j).getStringCellValue());
							else if (j >= 4) {
								if (j == 7) {
									pojo.put(String.format("%03d", j), "<th>" + row.getCell(j).getDateCellValue());
								} else {
									pojo.put(String.format("%03d", j), "<td>" + row.getCell(j).getStringCellValue());
								}
							}

						}
						pojo.put(String.format("%03d", 23), "<td>" + e.toString());
						pojoList.add(pojo);
					}
				}
			}
			return pojoList;
		} catch (Exception e) {
			return getError(e);
		} finally {
			clean(conn, prpStm, rs);
		}
	}

}
