package pojobase.interfaces;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import pojo.HoaHongThuCuoc;
import pojo.Mb6Program;
import pojo.MsaleReseller;
import pojo.SubReg;

/**
 * 
 * @author Nguyễn Thành Thủy
 * 
 */
@Service
@org.springframework.context.annotation.Scope("singleton")
public interface MsaleBase {

	List<Mb6Program> getMb6Programs(int Type) throws SQLException;

	List<SubReg> regSubsToProgram(int pro_id, int pro_type, String username, String[] preSubArray, String[] posSubArray,
			String month) throws SQLException;

	List<Map<String, String>> getKhdnNewActive(String account, Date from_date, Date to_date);

	List<Map<String, String>> getDistrict(String user_name, String province);

	List<Map<String, String>> getMsaleReseller(String user_name);

	List<Map<String, String>> getMsaleResellerDetail(String user_name, String province);

	List<Map<String, String>> updateMsaleResellerDetail(String user_name, String json);

	List<Map<String, String>> getAccount(String user_name, String json);

	List<Map<String, String>> getTraThuongDetail(String user_name, String json);

	List<Map<String, String>> getTraThuong(String account, String json);

	@PreAuthorize("hasAnyRole('BANHANGADMIN','KHDNADMIN','DEVELOPER')")
	List<Map<String, String>> updateAccount(String user_name, String json);

	List<Map<String, String>> getProgram(String user_name, String json);

	List<Map<String, String>> getProgramSub(String user_name, String json);

	@PreAuthorize("hasAnyRole('CAREPROGRAMMAN','BANHANGMANCONGTY','BANHANGADMIN','KHDNADMIN','KHDNMANCONGTY','CAREADMIN','CAREMANCONGTY')")
	List<Map<String, String>> programUpdateSub(String user_name, String json);

	@PreAuthorize("hasAnyRole('CAREPROGRAMMAN','BANHANGMANCONGTY','BANHANGADMIN','KHDNADMIN','KHDNMANCONGTY','CAREADMIN','CAREMANCONGTY')")
	List<Map<String, String>> updateProgram(String user_name, String json);

	List<MsaleReseller> getReselers(String user_name) throws SQLException;

	List<Map<String, String>> getTraThuongKichHoatTh(String user_name, String json);

	List<Map<String, String>> getTraThuongNapTheTh(String user_name, String json);

	List<Map<String, String>> getTraThuongPscTh(String user_name, String json);

	List<Map<String, String>> getTraThuongComboTh(String from_date, String to_date);

	List<Map<String, String>> CareHsrm(String user_name, String json);

	List<Map<String, String>> getNo3k3d(String user_name, String json);

	List<Map<String, String>> getTinhTrangThueBao(String user_name, String json);

	List<Map<String, String>> getChanMoMonthly(String user_name, String json);

	List<Map<String, String>> getChanMoMonthlyDetail(String user_name, String json);

	List<Map<String, String>> getChatLuongDonHang(String user_name, String json);

	List<Map<String, String>> getTinhTrangThueBaoDetail(String user_name, String json);

	List<Map<String, String>> getStockIsdnDetail(String user_name, String json);

	List<Map<String, String>> getKhdnNewActiveDetail(String user_name, String json);

	List<Map<String, String>> getKhsxkd(String user_name, String json);

	@PreAuthorize("hasAnyRole('KHDT_KHSXKD_NHAP')")
	List<Map<String, String>> updateKhsxkd(String user_name, String json);

	List<Map<String, String>> getChiPhiTrucTiep(String user_name, String json);

	@PreAuthorize("hasAnyRole('KHDT_KHSXKD_NHAP')")
	List<Map<String, String>> updateChiPhiTrucTiep(String user_name, String json);

	List<Map<String, String>> danhgiaChiPhiTrucTiep(String user_name, String json);

	@PreAuthorize("hasAnyRole('KHDT_KHSXKD_NHAP')")
	List<Map<String, String>> updateChiPhiGianTiep(String user_name, String json);

	List<Map<String, String>> getCpgtDesList(String user_name, String json);

	List<Map<String, String>> getChiPhiGianTiep(String user_name, String json);

	List<Map<String, String>> getChiPhiGianTiepArea(String user_name, String json);

	List<Map<String, String>> danhGiaChiPhiGianTiep(String user_name, String json);

	Map<String, HoaHongThuCuoc> getHoaHongThuCuocList(String user_name, String json);

	List<Map<String, String>> getDonViKhSxkd(String user_name, String json);

	List<Map<String, String>> ChiTietThuCuoc(String user_name, String json);

	void addLog(String string);

	@PreAuthorize("hasAnyRole('TTCPMANCONGTY')")
	List<Map<String, String>> tongHop(String user_name, String json);

	List<Map<String, String>> getAgent(String user_name, String json, int tonghop);

	List<Map<String, String>> hhtc_isclosed(String user_name, String json);

	@PreAuthorize("hasAnyRole('TTCPMANCONGTY')")
	List<Map<String, String>> hhtc_close(String user_name, String json);

	@PreAuthorize("hasAnyRole('TTCPMANCONGTY')")
	List<Map<String, String>> updateVungThuCuoc(String user_name, String json);

	@PreAuthorize("hasAnyRole('TTCPMANCONGTY','TTCPMANCHINHANH')")
	List<Map<String, String>> updateAgentKH(String user_name, String json);

	@PreAuthorize("hasAnyRole('TTCPMANCONGTY')")
	List<Map<String, String>> updateAgentParam(String user_name, String json);

	List<Map<String, String>> getThongTinTraThuong(String user_name, String string);

	List<Map<String, String>> lncc_roimang(String user_name, String json);

	List<Map<String, String>> sub_care_history(String user_name, String json);

	List<Map<String, String>> add_care_history(String user_name, String json);

	List<Map<String, String>> lncc_ketqua(String user_name, String json);

	List<Map<String, String>> lncc_result_detail(String user_name, String json);

	List<Map<String, String>> getLnccActList(String user_name, String json);
}
