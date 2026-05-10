/**
 * Copyright(C) 2026 Luvina
 * [EmployeeService.java], 23/04/2026 tranledat
 */
package com.luvina.la.service;

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.payload.request.EmployeeRequest;
import com.luvina.la.payload.response.EmployeeResponse;
import java.util.List;

/**
 * Interface Service định nghĩa các nghiệp vụ liên quan đến nhân viên.
 * Cung cấp các phương thức CRUD và kiểm tra tính hợp lệ phục vụ Controller.
 *
 * @author tranledat
 */
public interface EmployeeService {

    /**
     * Lấy tổng số nhân viên (không bao gồm admin) theo điều kiện tìm kiếm.
     *
     * @param employeeName Tên nhân viên cần lọc (hỗ trợ tìm kiếm LIKE, null = tất cả)
     * @param departmentId ID phòng ban cần lọc (null = tất cả phòng ban)
     * @return Tổng số lượng bản ghi nhân viên phù hợp với điều kiện (Long)
     */
    Long getTotalRecords(String employeeName, Long departmentId);

    /**
     * Lấy danh sách nhân viên có phân trang, tìm kiếm và sắp xếp bằng Native SQL.
     *
     * @param employeeName        Tên nhân viên cần tìm kiếm (hỗ trợ LIKE, null = tất cả)
     * @param departmentId        ID phòng ban cần lọc (null = tất cả phòng ban)
     * @param ordEmployeeName     Hướng sắp xếp theo tên nhân viên: "ASC" hoặc "DESC"
     * @param ordCertificationName Hướng sắp xếp theo tên chứng chỉ: "ASC" hoặc "DESC"
     * @param ordEndDate          Hướng sắp xếp theo ngày hết hạn chứng chỉ: "ASC" hoặc "DESC"
     * @param offset              Vị trí bắt đầu lấy dữ liệu (dùng để phân trang, tính bằng: (trang - 1) × limit)
     * @param limit               Số lượng bản ghi tối đa trả về trên một trang
     * @return Danh sách EmployeeDTO chứa thông tin nhân viên và chứng chỉ tương ứng
     */
    List<EmployeeDTO> getEmployees(String employeeName, Long departmentId, String ordEmployeeName,
            String ordCertificationName, String ordEndDate, Integer offset, Integer limit);

    /**
     * Thêm mới một nhân viên vào hệ thống, bao gồm cả thông tin chứng chỉ (nếu có).
     * Mật khẩu được mã hóa trước khi lưu vào Database.
     *
     * @param employeeRequest Đối tượng chứa toàn bộ thông tin nhân viên và chứng chỉ từ Client
     */
    void addEmployee(EmployeeRequest employeeRequest);

    /**
     * Lấy thông tin chi tiết của một nhân viên theo ID,
     * bao gồm thông tin cá nhân, phòng ban và danh sách chứng chỉ.
     *
     * @param employeeId ID của nhân viên cần lấy thông tin
     * @return EmployeeResponse chứa đầy đủ thông tin chi tiết nhân viên và chứng chỉ
     */
    EmployeeResponse getEmployeeDetailById(Long employeeId);

    /**
     * Xóa một nhân viên khỏi hệ thống.
     * Trước khi xóa, sẽ xóa toàn bộ bản ghi chứng chỉ liên quan để tránh vi phạm ràng buộc khóa ngoại.
     *
     * @param employeeId ID của nhân viên cần xóa
     */
    void deleteEmployee(Long employeeId);

    /**
     * Cập nhật thông tin của một nhân viên đã tồn tại trong hệ thống.
     * Chỉ cập nhật mật khẩu khi người dùng cung cấp mật khẩu mới (không rỗng).
     * Xóa và tạo lại toàn bộ chứng chỉ để đảm bảo tính nhất quán dữ liệu.
     *
     * @param employeeRequest Đối tượng chứa dữ liệu cần cập nhật từ Client
     */
    void updateEmployee(EmployeeRequest employeeRequest);

    /**
     * Kiểm tra sự tồn tại của nhân viên theo ID.
     * Dùng để xác nhận nhân viên có tồn tại trong DB trước khi thực hiện thao tác Edit/Delete.
     *
     * @param employeeId ID của nhân viên cần kiểm tra
     * @return true nếu nhân viên tồn tại, false nếu không tìm thấy
     */
    boolean checkExistsEmployeeById(Long employeeId);

    /**
     * Kiểm tra sự trùng lặp của Login ID trong hệ thống.
     * Khi cập nhật (Edit), bỏ qua bản ghi của chính nhân viên đó.
     *
     * @param loginId    Login ID cần kiểm tra trùng lặp
     * @param employeeId ID của nhân viên hiện tại (null nếu đang thêm mới)
     * @return true nếu Login ID đã được sử dụng bởi nhân viên khác, false nếu chưa trùng
     */
    boolean checkExistsEmployeeByLoginId(String loginId, Long employeeId);
}
