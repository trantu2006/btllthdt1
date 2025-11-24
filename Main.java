import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// ======= Lớp Tài liệu và các lớp con =======
class TaiLieu implements Serializable {
    protected String ma, ten, tacGia;
    protected int namXB;

    public TaiLieu(String ma, String ten, String tacGia, int namXB) {
        this.ma = ma; this.ten = ten; this.tacGia = tacGia; this.namXB = namXB;
    }

    public void hienThi() {
        System.out.printf("Mã: %s | Tên: %s | Tác giả: %s | Năm XB: %d\n", ma, ten, tacGia, namXB);
    }

    public String getMa() { return ma; }
    public String getTen() { return ten; }
    public String getTacGia() { return tacGia; }
    public int getNamXB() { return namXB; }
    public void setTen(String ten) { this.ten = ten; }
    public void setTacGia(String tacGia) { this.tacGia = tacGia; }
    public void setNamXB(int namXB) { this.namXB = namXB; }
}

class Sach extends TaiLieu {
    public Sach(String ma, String ten, String tacGia, int namXB) { super(ma, ten, tacGia, namXB); }
}

class TapChi extends TaiLieu {
    public TapChi(String ma, String ten, String tacGia, int namXB) { super(ma, ten, tacGia, namXB); }
}

class Bao extends TaiLieu {
    public Bao(String ma, String ten, String tacGia, int namXB) { super(ma, ten, tacGia, namXB); }
}

// ======= Lớp Bạn đọc =======
class BanDoc implements Serializable {
    private String maThe, hoTen, diaChi, loaiThe;
    public BanDoc(String maThe, String hoTen, String diaChi, String loaiThe) {
        this.maThe = maThe; this.hoTen = hoTen; this.diaChi = diaChi; this.loaiThe = loaiThe;
    }

    public void hienThi() {
        System.out.printf("Mã thẻ: %s | Họ tên: %s | Địa chỉ: %s | Loại thẻ: %s\n", maThe, hoTen, diaChi, loaiThe);
    }

    public String getMaThe() { return maThe; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public void setLoaiThe(String loaiThe) { this.loaiThe = loaiThe; }
}

// ======= Lớp Mượn trả =======
class MuonTra implements Serializable {
    private String maThe, maTaiLieu;
    private LocalDate ngayMuon, ngayTra;

    public MuonTra(String maThe, String maTaiLieu, LocalDate ngayMuon) {
        this.maThe = maThe; this.maTaiLieu = maTaiLieu; this.ngayMuon = ngayMuon;
    }

    public void traSach(LocalDate ngayTra) { this.ngayTra = ngayTra; }

    public double tinhTienPhat() {
        if (ngayTra == null) return 0;
        long soNgayTre = ChronoUnit.DAYS.between(ngayMuon, ngayTra) - 7;
        return soNgayTre > 0 ? soNgayTre * 5000 : 0;
    }

    public void hienThi() {
        System.out.printf("Mã thẻ: %s | Mã tài liệu: %s | Ngày mượn: %s | Ngày trả: %s | Tiền phạt: %.0f VND\n",
                maThe, maTaiLieu, ngayMuon, ngayTra, tinhTienPhat());
    }

    public String getMaTaiLieu() { return maTaiLieu; }
}

// ======= Lớp Quản lý thư viện =======
class QuanLyThuVien {
    public ArrayList<TaiLieu> taiLieuList = new ArrayList<>();
    public ArrayList<BanDoc> banDocList = new ArrayList<>();
    public ArrayList<MuonTra> muonTraList = new ArrayList<>();

    // Thêm tài liệu
    public void themTaiLieu(TaiLieu tl) { taiLieuList.add(tl); }

    // Sửa tài liệu
    public void suaTaiLieu(String ma, String ten, String tacGia, int namXB) {
        for (TaiLieu tl : taiLieuList) {
            if (tl.getMa().equals(ma)) {
                tl.setTen(ten); tl.setTacGia(tacGia); tl.setNamXB(namXB);
                System.out.println("Cập nhật thành công!");
                return;
            }
        }
        System.out.println("Không tìm thấy tài liệu!");
    }

    // Xóa tài liệu
    public void xoaTaiLieu(String ma) { taiLieuList.removeIf(tl -> tl.getMa().equals(ma)); }

    // Tìm kiếm
    public void timKiem(String tuKhoa) {
        for (TaiLieu tl : taiLieuList) {
            if (tl.getTen().toLowerCase().contains(tuKhoa.toLowerCase()) ||
                tl.getTacGia().toLowerCase().contains(tuKhoa.toLowerCase()) ||
                String.valueOf(tl.getNamXB()).equals(tuKhoa)) {
                tl.hienThi();
            }
        }
    }

    // Thống kê sách mượn nhiều nhất
    public void thongKe() {
        HashMap<String, Integer> count = new HashMap<>();
        for (MuonTra mt : muonTraList) {
            count.put(mt.getMaTaiLieu(), count.getOrDefault(mt.getMaTaiLieu(), 0) + 1);
        }
        count.entrySet().stream()
             .sorted((a, b) -> b.getValue() - a.getValue())
             .limit(5)
             .forEach(e -> System.out.println("Mã tài liệu: " + e.getKey() + " | Số lượt mượn: " + e.getValue()));
    }

    // Lưu dữ liệu
    public <T> void luuFile(String fileName, ArrayList<T> list) {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(list);
        } catch(Exception e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> docFile(String fileName) {
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return (ArrayList<T>) in.readObject();
        } catch(Exception e) { return new ArrayList<>(); }
    }
}

// ======= Main Menu =======
public class Main {
    static Scanner sc = new Scanner(System.in);
    static QuanLyThuVien qltv = new QuanLyThuVien();

    public static void main(String[] args) {
        // Load dữ liệu
        qltv.taiLieuList = qltv.docFile("tailieu.dat");
        qltv.banDocList = qltv.docFile("bandoc.dat");
        qltv.muonTraList = qltv.docFile("muontra.dat");

        int luaChon;
        do {
            System.out.println("\n=== QUAN LY THU VIEN ===");
            System.out.println("1. Quan ly tai lieu");
            System.out.println("2. Quan ly tai lieu");
            System.out.println("3. Muon\tra sach");
            System.out.println("4. Tim kiem tai lieu");
            System.out.println("5. Thong ke sach muon nhieu nhat");
            System.out.println("0. Thoat");
            System.out.print("Chọn: "); luaChon = Integer.parseInt(sc.nextLine());

            switch(luaChon) {
                case 1: menuTaiLieu(); break;
                case 2: menuBanDoc(); break;
                case 3: menuMuonTra(); break;
                case 4:
                    System.out.print("Nhập từ khóa tìm kiếm: "); String tuKhoa = sc.nextLine();
                    qltv.timKiem(tuKhoa); break;
                case 5: qltv.thongKe(); break;
                case 0:
                    qltv.luuFile("tailieu.dat", qltv.taiLieuList);
                    qltv.luuFile("bandoc.dat", qltv.banDocList);
                    qltv.luuFile("muontra.dat", qltv.muonTraList);
                    System.out.println("Đã lưu dữ liệu & Thoát!"); break;
                default: System.out.println("Lựa chọn không hợp lệ!"); break;
            }
        } while(luaChon != 0);
    }

    static void menuTaiLieu() {
        System.out.println("\n--- QUẢN LÝ TÀI LIỆU ---");
        System.out.println("1. Them tai lieu");
        System.out.println("2. Sua tai lieu");
        System.out.println("3. Xoa tai lieu");
        System.out.println("4. Hien thi tat ca");
        System.out.print("Chọn: "); int c = Integer.parseInt(sc.nextLine());
        switch(c) {
            case 1:
                System.out.print("Loai (Sach/TapChi/Bao): "); String loai = sc.nextLine();
                System.out.print("Ma: "); String ma = sc.nextLine();
                System.out.print("Ten: "); String ten = sc.nextLine();
                System.out.print("Tac gia: "); String tacGia = sc.nextLine();
                System.out.print("Nam xuat ban: "); int namXB = Integer.parseInt(sc.nextLine());
                if(loai.equalsIgnoreCase("Sach")) qltv.themTaiLieu(new Sach(ma,ten,tacGia,namXB));
                else if(loai.equalsIgnoreCase("TapChi")) qltv.themTaiLieu(new TapChi(ma,ten,tacGia,namXB));
                else qltv.themTaiLieu(new Bao(ma,ten,tacGia,namXB));
                break;
            case 2:
                System.out.print("Nhập mã tài liệu cần sửa: "); String maSua = sc.nextLine();
                System.out.print("Tên mới: "); String tenMoi = sc.nextLine();
                System.out.print("Tác giả mới: "); String tgMoi = sc.nextLine();
                System.out.print("Năm xuất bản mới: "); int namMoi = Integer.parseInt(sc.nextLine());
                qltv.suaTaiLieu(maSua, tenMoi, tgMoi, namMoi); break;
            case 3:
                System.out.print("Nhập mã tài liệu cần xóa: "); String maXoa = sc.nextLine();
                qltv.xoaTaiLieu(maXoa); break;
            case 4:
                for(TaiLieu tl: qltv.taiLieuList) tl.hienThi(); break;
            default: System.out.println("Lựa chọn không hợp lệ!"); break;
        }
    }

    static void menuBanDoc() {
        System.out.println("\n--- QUẢN LÝ BẠN ĐỌC ---");
        System.out.println("1. Thêm bạn đọc");
        System.out.println("2. Sửa bạn đọc");
        System.out.println("3. Xóa bạn đọc");
        System.out.println("4. Hiển thị tất cả");
        System.out.print("Chọn: "); int c = Integer.parseInt(sc.nextLine());
        switch(c) {
            case 1:
                System.out.print("Mã thẻ: "); String maThe = sc.nextLine();
                System.out.print("Họ tên: "); String hoTen = sc.nextLine();
                System.out.print("Địa chỉ: "); String diaChi = sc.nextLine();
                System.out.print("Loại thẻ: "); String loaiThe = sc.nextLine();
                qltv.banDocList.add(new BanDoc(maThe,hoTen,diaChi,loaiThe)); break;
            case 2:
                System.out.print("Nhập mã thẻ cần sửa: "); String maSua = sc.nextLine();
                for(BanDoc bd: qltv.banDocList) {
                    if(bd.getMaThe().equals(maSua)) {
                        System.out.print("Họ tên mới: "); bd.setHoTen(sc.nextLine());
                        System.out.print("Địa chỉ mới: "); bd.setDiaChi(sc.nextLine());
                        System.out.print("Loại thẻ mới: "); bd.setLoaiThe(sc.nextLine());
                        System.out.println("Đã cập nhật!"); break;
                    }
                } break;
            case 3:
                System.out.print("Nhập mã thẻ cần xóa: "); String maXoa = sc.nextLine();
                qltv.banDocList.removeIf(bd -> bd.getMaThe().equals(maXoa)); break;
            case 4:
                for(BanDoc bd: qltv.banDocList) bd.hienThi(); break;
            default: System.out.println("Lựa chọn không hợp lệ!"); break;
        }
    }

    static void menuMuonTra() {
        System.out.println("\n--- MƯỢN/TRẢ SÁCH ---");
        System.out.println("1. Mượn sách");
        System.out.println("2. Trả sách");
        System.out.println("3. Hiển thị tất cả giao dịch");
        System.out.print("Chọn: "); int c = Integer.parseInt(sc.nextLine());
        switch(c) {
            case 1:
                System.out.print("Mã thẻ: "); String maThe = sc.nextLine();
                System.out.print("Mã tài liệu: "); String maTL = sc.nextLine();
                System.out.print("Ngày mượn (yyyy-mm-dd): "); LocalDate ngayMuon = LocalDate.parse(sc.nextLine());
                qltv.muonTraList.add(new MuonTra(maThe,maTL,ngayMuon)); break;
            case 2:
                System.out.print("Mã tài liệu cần trả: "); String maTra = sc.nextLine();
                System.out.print("Ngày trả (yyyy-mm-dd): "); LocalDate ngayTra = LocalDate.parse(sc.nextLine());
                for(MuonTra mt: qltv.muonTraList)
                    if(mt.getMaTaiLieu().equals(maTra) && mt.tinhTienPhat() == 0) {
                        mt.traSach(ngayTra); break;
                    } break;
            case 3:
                for(MuonTra mt: qltv.muonTraList) mt.hienThi(); break;
            default: System.out.println("Lựa chọn không hợp lệ!"); break;
        }
    }
}
