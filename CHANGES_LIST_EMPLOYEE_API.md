# Huong dan va giai thich - API List Employee

## Tong quan

Implement API `GET /employee` de lay danh sach nhan vien theo dieu kien tim kiem, co ho tro sap xep va phan trang.
API nay yeu cau JWT token (Bearer) de truy cap.

---

## Danh sach file thay doi

### FILE MOI TAO (5 file)

---

### 1. `dto/EmployeeListDTO.java`

**Duong dan:** `src/main/java/com/luvina/la/dto/EmployeeListDTO.java`

**Muc dich:** DTO (Data Transfer Object) chua du lieu cua moi nhan vien trong danh sach response.

**Cac field:**
| Field | Kieu | Mo ta |
|-------|------|-------|
| employeeId | Long | ID nhan vien |
| employeeName | String | Ten nhan vien |
| employeeBirthDate | String | Ngay sinh (format: yyyy/MM/dd) |
| departmentName | String | Ten phong ban |
| employeeEmail | String | Email |
| employeeTelephone | String | So dien thoai |
| certificationName | String | Ten chung chi tieng Nhat (null neu khong co) |
| endDate | String | Ngay het han chung chi (format: yyyy/MM/dd, null neu khong co) |
| score | BigDecimal | Diem chung chi (null neu khong co) |

**Giai thich:** 
- Dung `String` cho ngay thay vi `LocalDate` de format san `yyyy/MM/dd` truoc khi tra ve JSON.
- `certificationName`, `endDate`, `score` co the null vi dung LEFT JOIN (nhan vien co the khong co chung chi).

---

### 2. `payload/EmployeeListResponse.java`

**Duong dan:** `src/main/java/com/luvina/la/payload/EmployeeListResponse.java`

**Muc dich:** Wrapper class cho response cua API, co 2 truong hop:

**Truong hop thanh cong:**
```json
{
    "code": "200",
    "totalRecords": 2,
    "employees": [...]
}
```

**Truong hop loi:**
```json
{
    "code": "500",
    "message": {
        "code": "ER021",
        "params": []
    }
}
```

**Giai thich:**
- Annotation `@JsonInclude(JsonInclude.Include.NON_NULL)` dam bao cac field null se KHONG xuat hien trong JSON response. Vi du: khi thanh cong thi `message` se khong hien, khi loi thi `totalRecords` va `employees` se khong hien.
- Co 2 constructor tuong ung 2 truong hop: thanh cong va loi.

---

### 3. `payload/MessageResponse.java`

**Duong dan:** `src/main/java/com/luvina/la/payload/MessageResponse.java`

**Muc dich:** Wrapper cho error message, chua `code` (ma loi) va `params` (tham so cua loi).

**Vi du:**
- ER021: `{code: "ER021", params: []}` - Thu tu sap xep phai la ASC hoac DESC
- ER018: `{code: "ER018", params: ["オフセット"]}` - Offset phai la so nguyen duong

---

### 4. `controller/EmployeeController.java`

**Duong dan:** `src/main/java/com/luvina/la/controller/EmployeeController.java`

**Muc dich:** Controller xu ly request `GET /employee`.

**Request Parameters:**
| Parameter | Bat buoc | Mo ta |
|-----------|----------|-------|
| employee_name | Khong | Tim kiem theo ten nhan vien (LIKE %name%) |
| department_id | Khong | Loc theo phong ban |
| ord_employee_name | Khong | Sap xep theo ten: ASC hoac DESC |
| ord_certification_name | Khong | Sap xep theo ten chung chi: ASC hoac DESC |
| ord_end_date | Khong | Sap xep theo ngay het han: ASC hoac DESC |
| offset | Khong | Vi tri bat dau lay (mac dinh: 0) |
| limit | Khong | So luong ban ghi toi da (mac dinh: 5) |

**Giai thich:**
- Tat ca params deu la `required = false` (khong bat buoc).
- Params duoc nhan dang `String` de validate o tang Service truoc khi parse.
- Endpoint `/employee` yeu cau JWT token vi khong nam trong `ENDPOINTS_PUBLIC` cua SecurityConfiguration.

---

### 5. `payload/MessageResponse.java`

(Da mo ta o muc 3)

---

### FILE CHINH SUA (2 file)

---

### 6. `repository/EmployeeRepository.java` (CHINH SUA)

**Duong dan:** `src/main/java/com/luvina/la/repository/EmployeeRepository.java`

**Thay doi:** Them 2 method moi, giu nguyen 2 method cu.

**Method cu (KHONG THAY DOI):**
- `findByEmployeeLoginId()` - dung cho login
- `findByEmployeeId()` - dung cho tim nhan vien theo ID

**Method moi 1: `countEmployees()`**
```sql
SELECT COUNT(e.employee_id) 
FROM employees e 
INNER JOIN departments d ON e.department_id = d.department_id 
WHERE (:employeeName IS NULL OR e.employee_name LIKE CONCAT('%', :employeeName, '%')) 
AND (:departmentId IS NULL OR e.department_id = :departmentId)
```
- Dem tong so nhan vien theo dieu kien search (employee_name va department_id).
- Dung `INNER JOIN departments` vi moi nhan vien phai co phong ban.
- Dieu kien WHERE la dynamic: chi ap dung khi param khong null.

**Method moi 2: `getEmployees()`**
```sql
SELECT e.employee_id, e.employee_name, e.employee_birth_date, 
       d.department_name, e.employee_email, e.employee_telephone, 
       c.certification_name, ec.end_date, ec.score 
FROM employees e 
INNER JOIN departments d ON e.department_id = d.department_id 
LEFT JOIN employees_certifications ec ON e.employee_id = ec.employee_id 
LEFT JOIN certifications c ON ec.certification_id = c.certification_id 
WHERE ... 
ORDER BY ... 
LIMIT :limit OFFSET :offset
```
- `INNER JOIN departments`: Moi nhan vien phai co phong ban.
- `LEFT JOIN employees_certifications`: Nhan vien co the KHONG co chung chi.
- `LEFT JOIN certifications`: Lay ten chung chi tu bang certifications.
- Sort dong bang `CASE WHEN`: Chi sort theo truong nao khi param tuong ung khong null.
- Mac dinh luon sort theo `employee_id ASC` o cuoi cung.
- Phan trang bang `LIMIT` va `OFFSET`.

**Giai thich ve quan he N-N:**
```
employees (1) --- (*) employees_certifications (*) --- (1) certifications
```
- Bang `employees_certifications` la bang trung gian luu: employee_id, certification_id, start_date, end_date, score.
- Dung LEFT JOIN nen nhan vien khong co chung chi van duoc hien thi (certificationName, endDate, score se la null).

---

### 7. `service/EmployeeService.java` (CHINH SUA - VIET LAI HOAN TOAN)

**Duong dan:** `src/main/java/com/luvina/la/service/EmployeeService.java`

**Truoc do:** File rong, chi co annotation `@Service`.

**Sau khi sua:** Chua toan bo business logic cua API List Employee.

**Flow xu ly (theo dung spec TKAPI_ListEmployee):**

```
Buoc 1: Validate parameters
  |-- 1.1: Kiem tra ord_employee_name, ord_certification_name, ord_end_date 
  |        phai la "ASC" hoac "DESC" -> Neu sai: tra ve ER021
  |-- 1.2: Kiem tra offset phai la so nguyen duong -> Neu sai: tra ve ER018
  |-- 1.3: Kiem tra limit phai la so nguyen duong -> Neu sai: tra ve ER018
  |
Buoc 2: Get danh sach nhan vien
  |-- 2.1: Dem tong so nhan vien (countEmployees)
  |        Neu totalRecords = 0 -> Tra ve {code: 200, totalRecords: 0, employees: []}
  |-- 2.2: Lay danh sach nhan vien (getEmployees) voi sort va phan trang
  |
Buoc 3: Tao response
  |-- Khong loi: {code: 200, totalRecords: X, employees: [...]}
  |-- Co loi:    {code: 500, message: {code: "ERxxx", params: [...]}}
```

**Cac method chinh:**
- `getEmployees()`: Method chinh, dieu phoi toan bo flow.
- `validateParams()`: Validate cac tham so dau vao, tra ve ma loi hoac null (khong loi).
- `isPositiveInteger()`: Kiem tra mot String co phai so nguyen duong hay khong.
- `buildErrorResponse()`: Tao response loi voi ma loi va params tuong ung.

**Xu ly loi:**
- Toan bo logic duoc boc trong try-catch. Neu co exception bat ngo (vi du loi database), tra ve ER015 ("He thong dang co loi").

---

## Cach test API

### 1. Dang nhap de lay token

```
POST http://localhost:8085/login
Content-Type: application/json

{
    "username": "admin",
    "password": "admin"
}
```

Response se tra ve `accessToken`.

### 2. Goi API List Employee

```
GET http://localhost:8085/employee?employee_name=&department_id=&ord_employee_name=ASC&ord_certification_name=ASC&ord_end_date=DESC&offset=0&limit=30
Authorization: Bearer <accessToken o buoc 1>
```

### 3. Vi du cac truong hop test

| Test case | URL | Ket qua mong doi |
|-----------|-----|------------------|
| Lay tat ca | `/employee` | code: 200, tra ve toi da 5 nhan vien (limit mac dinh) |
| Tim theo ten | `/employee?employee_name=Nguyen` | code: 200, chi tra ve nhan vien co ten chua "Nguyen" |
| Loc theo phong ban | `/employee?department_id=1` | code: 200, chi tra ve nhan vien phong ban ID=1 |
| Sort theo ten ASC | `/employee?ord_employee_name=ASC` | code: 200, sap xep theo ten tang dan |
| Phan trang | `/employee?offset=0&limit=10` | code: 200, lay 10 ban ghi dau |
| Loi sort sai | `/employee?ord_employee_name=XYZ` | code: 500, message: {code: "ER021", params: []} |
| Loi offset sai | `/employee?offset=-1` | code: 500, message: {code: "ER018", params: ["オフセット"]} |
| Khong co token | `/employee` (khong co header Authorization) | HTTP 401 Unauthorized |

---

## Cau truc thu muc sau khi thay doi

```
src/main/java/com/luvina/la/
├── controller/
│   ├── AuthController.java          (KHONG DOI)
│   ├── HomeController.java          (KHONG DOI)
│   └── EmployeeController.java      *** MOI ***
├── dto/
│   ├── EmployeeDTO.java             (KHONG DOI)
│   └── EmployeeListDTO.java         *** MOI ***
├── entity/
│   ├── Employee.java                (KHONG DOI)
│   ├── Department.java              (KHONG DOI)
│   ├── Certification.java           (KHONG DOI)
│   └── EmployeeCertification.java   (KHONG DOI)
├── payload/
│   ├── LoginRequest.java            (KHONG DOI)
│   ├── LoginResponse.java           (KHONG DOI)
│   ├── EmployeeListResponse.java    *** MOI ***
│   └── MessageResponse.java         *** MOI ***
├── repository/
│   └── EmployeeRepository.java      *** CHINH SUA (them 2 method) ***
├── service/
│   └── EmployeeService.java         *** CHINH SUA (viet lai hoan toan) ***
├── mapper/
│   └── EmployeeMapper.java          (KHONG DOI)
├── validation/
│   └── ValidateUtil.java            (KHONG DOI)
└── config/
    └── ...                          (KHONG DOI)
```

**Tong ket: 4 file moi, 2 file chinh sua, 0 file entity/config bi thay doi.**
