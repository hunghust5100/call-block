# 🛡️ BrandBlock - Ứng Dụng Chặn Cuộc Gọi & SMS SPAM Ngầm (<50ms)

**BrandBlock** là ứng dụng Android Native (Java/Android SDK) giúp nhận diện và tự động chặn ngầm các cuộc gọi, tin nhắn rác, quấy rối (SPAM, Tín dụng đen, Bất động sản, Chứng khoán quốc tế...) với tốc độ phản hồi dưới **50ms**, hoàn toàn **100% Offline** không lưu dữ liệu lên máy chủ.

---

## 🚀 Tải Ngay File APK

Tải và cài đặt ứng dụng trực tiếp từ bộ mã nguồn:
👉 **[Tải file APK (app-debug.apk)](releases/app-debug.apk)**

---

## ✨ Tính Năng Nổi Bật

- **🛡️ Chặn Cuộc Gọi & SMS Ngầm Tốc Độ Cao (<50ms)**: Tích hợp `CallScreeningService` chuẩn Android Native, ngắt cuộc gọi rác và lọc tin nhắn SPAM tức thì không gây đổ chuông hoặc làm phiền.
- **🏷️ Quản Lý Theo Danh Sách & Đầu Số Quy Tắc**: Gom nhóm các số điện thoại quấy rối theo danh sách dịch vụ (như *Tín dụng SPAM*, *Bất động sản*, *Sàn Forex*...). Hỗ trợ chặn theo đầu số quy tắc (ví dụ: `024730...`, `190066xx`).
- **💬 Nhật Ký Tích Hợp (Cuộc Gọi & SMS)**: Xem lịch sử nhật ký cuộc gọi và tin nhắn SMS gần đây, hỗ trợ nút **Chặn nhanh** trực tiếp từ nhật ký.
- **🔒 Bảo Vệ Quyền Riêng Tư 100% Offline**: Tất cả dữ liệu danh sách chặn và lịch sử lưu trữ cục bộ trên thiết bị qua SQLite (Room DB), không tải dữ liệu cá nhân lên mạng.
- **📊 Thống Kê & Lịch Sử Chi Tiết**: Theo dõi tổng số cuộc gọi đã chặn, tổng số tin nhắn đã chặn và danh sách quấy rối tần suất cao nhất.
- **📱 Giao Diện Material 3 / Google Pixel Modern**: Thiết kế tinh tế, trực quan, hỗ trợ màn hình Onboarding vuốt lướt hướng dẫn lần đầu mở app.

---

## 📸 Ảnh Chụp Màn Hình Thật Chạy Trên Giả Lập

| Nhật Ký Cuộc Gọi | Nhật Ký Tin Nhắn |
| :---: | :---: |
| ![Nhật ký cuộc gọi](docs/screenshots/screen_call_log.png) | ![Nhật ký tin nhắn](docs/screenshots/screen_sms_log.png) |

| Thống Kê & Lịch Sử | Danh Sách Chặn | Cài Đặt Dịch Vụ |
| :---: | :---: | :---: |
| ![Thống kê](docs/screenshots/screen_stats.png) | ![Danh sách chặn](docs/screenshots/screen_brand_list.png) | ![Cài đặt](docs/screenshots/screen_settings.png) |

---

## 🛠️ Kiến Trúc & Công Nghệ Key

- **Ngôn ngữ**: Java (Android Native Standard)
- **Kiến trúc**: MVVM / Repository Pattern + Room Database
- **Thư viện chính**:
  - `androidx.room`: Quản lý SQLite Database Offline.
  - `com.google.android.material`: Giao diện Material Design 3.
  - `CallScreeningService`: Dịch vụ chặn cuộc gọi ngầm cấp hệ thống (<50ms).
  - `ContentResolver`: Đọc nhật ký cuộc gọi và tin nhắn SMS.
  - `ViewPager2` & `TabLayout`: Màn hình lướt giới thiệu Onboarding.

---

## 📋 Hướng Dẫn Cấu Hình & Cấp Quyền

1. Tải và cài đặt file [`releases/app-debug.apk`](releases/app-debug.apk) trên điện thoại Android (API 29+ / Android 10 trở lên).
2. Lần đầu mở ứng dụng, vuốt qua các slide Onboarding và bấm **Cho phép** cấp quyền đọc nhật ký cuộc gọi/SMS.
3. Khi hệ thống hỏi cấp **Call Screening Role** (Vai trò Chặn cuộc gọi ngầm), bấm **Cho phép** để kích hoạt dịch vụ ngắt cuộc gọi rác ngầm tự động.

---

*Phát triển với ❤️ cho nền tảng Android Native Java.*
