package servlets; // 【確認】servletsパッケージに属している

import java.io.File;
import java.io.IOException;
import java.nio.file.Files; // ファイル操作のimportを追加
import java.nio.file.Path;   // ファイル操作のimportを追加
import java.nio.file.Paths;  // ファイル操作のimportを追加
import java.nio.file.StandardOpenOption; // ファイル操作のimportを追加
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;



@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, 
    maxFileSize = 1024 * 1024 * 5, // 5MB
    maxRequestSize = 1024 * 1024 * 10 // 10MB
)

@WebServlet("/GameServlet")
public class GameServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOGGER = Logger.getLogger(GameServlet.class.getName());
    
    // アップロードディレクトリの絶対パス
    private String UPLOAD_DIR; 
    
    /**
     * サーブレット初期化時にアップロードディレクトリの絶対パスを決定します。
     */
    @Override
    public void init() throws ServletException {
        // 【変更なし】固定の絶対パス
        UPLOAD_DIR = "C:\\Users\\236046\\Documents\\resource"; 
        
        LOGGER.info("設定されたアップロードパス: " + UPLOAD_DIR);
    }

    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        LOGGER.info("--- GameServlet: doPost メソッド開始 (データ引き渡し処理へ修正) ---");
        request.setCharacterEncoding("UTF-8");
        
        // init() で設定された UPLOAD_DIR を使用
        final String savePath = UPLOAD_DIR;
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());

        try {
            // =======================================================
            // 1. 保存先ディレクトリの準備
            // =======================================================
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                if (saveDir.mkdirs()) {
                    LOGGER.info("アップロードディレクトリを作成しました: " + savePath);
                } else {
                    throw new IOException("アップロードディレクトリの作成に失敗しました。権限を確認してください: " + savePath);
                }
            }
            LOGGER.info("--- GameServlet: 保存処理の開始 (ディレクトリ: " + savePath + ") ---");
            
            // =======================================================
            // 2. フォームデータの取得とアップロードされたファイルの保存
            // =======================================================
            
            Part frontPart = request.getPart("upload_image_front");
            String frontFilePath = processAndSavePart(frontPart, savePath, timestamp, "_front_");

            Part backPart = request.getPart("upload_image_back");
            String backFilePath = processAndSavePart(backPart, savePath, timestamp, "_back_");
            
            // =======================================================
            // 3. フォームデータの取得とCSVファイルへの追記
            // =======================================================
            
            String itemId = request.getParameter("item_id");
            String paymentMethod = request.getParameter("payment_method");
            String number = request.getParameter("number"); // カード番号
            String day = request.getParameter("day");        // 有効期限
            String code = request.getParameter("code");      // セキュリティコード

            String fullNumber = number != null ? number : ""; 
            String codeValue = code != null ? code : ""; 
            
            // --------------------------------------------------------------------------------
            // ★★★ 修正箇所: JSPに渡すデータを用意する (IDに応じて動的に変更) ★★★
            // --------------------------------------------------------------------------------
            
            String itemName = "不明なアイテム";
            String itemPriceStr = "0";
            
            // 【重要】itemId (注文ID) に応じて、商品名と価格を設定する
            // ストアページ (store_n.html) の data-id に基づいて判定する
            switch (itemId) {
                case "stone_s":
                    itemName = "ブルートフォース攻撃";
                    itemPriceStr = "300";
                    break;
                case "stone_m":
                    itemName = "SQLインジェクション攻撃";
                    itemPriceStr = "750";
                    break;
                case "stone_l":
                    itemName = "クロスサイトスクリプティング";
                    itemPriceStr = "2900";
                    break;
                case "pass_v1":
                    itemName = "先生の全知識";
                    itemPriceStr = "400000";
                    break;
                default:
                    // itemIdがnullまたは一致しない場合の処理
                    LOGGER.warning("不明なItemIDまたはIDが未設定: " + itemId);
                    // 注文完了ページに渡す情報がないため、フォールバック値を設定
                    itemName = "不明なアイテム";
                    itemPriceStr = "0";
                    break;
            }
            
            // CSVのフィールドを準備 (itemId, paymentMethod, number, day, code はフォームから取得済み)
            String[] csvFields = new String[]{
                timestamp,
                itemId != null ? itemId : "N/A",
                paymentMethod != null ? paymentMethod : "N/A",
                fullNumber,
                day != null ? day : "",
                codeValue,
                frontFilePath,
                backFilePath
            };
            
            // CSVファイル名とパス決定
            String csvFileName = "ORDER_DATA.csv";
            Path csvFilePath = Paths.get(savePath, csvFileName);

            // ファイルが存在しない場合、ヘッダー行を追加 (中略: CSVヘッダー処理)
             if (Files.notExists(csvFilePath)) {
                 byte[] bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
                 String header = "タイムスタンプ,注文ID,支払方法,カード番号(完全),有効期限,セキュリティコード,表面画像ファイルパス,裏面画像ファイルパス\n";
                 byte[] headerBytes = header.getBytes("UTF-8");
                 byte[] combinedBytes = new byte[bom.length + headerBytes.length];
                 System.arraycopy(bom, 0, combinedBytes, 0, bom.length);
                 System.arraycopy(headerBytes, 0, combinedBytes, bom.length, headerBytes.length);
                 Files.write(csvFilePath, combinedBytes);
                 LOGGER.info(" - CSVファイルヘッダーを作成しました (BOM付き): " + csvFileName);
             }
            
            // CSV形式のレコードを作成
            String csvRecord = Arrays.stream(csvFields)
                                     .map(s -> "\"" + s.replace("\"", "\"\"") + "\"") 
                                     .collect(Collectors.joining(",")) + "\n";
            
            // CSVファイルにレコードを追記
            Files.write(csvFilePath, csvRecord.getBytes("UTF-8"), StandardOpenOption.APPEND);
            LOGGER.info(" - 注文情報をCSVファイルに追記しました: " + csvFileName);
            
            // --------------------------------------------------------------------------------
            // ★★★ JSPへ渡すデータをリクエストスコープにセット ★★★
            // --------------------------------------------------------------------------------
            request.setAttribute("itemName", itemName);
            request.setAttribute("itemPrice", itemPriceStr);
            request.setAttribute("paymentMethod", paymentMethod);
            
            LOGGER.info(" - 注文情報をリクエストスコープにセットしました: " + itemName + " (¥" + itemPriceStr + ")");
            
            // --------------------------------------------------------------------------------
            // ★★★ 成功後のJSPへのフォワード（データ引き渡し） ★★★
            // --------------------------------------------------------------------------------
            
            // データ（リクエストスコープ）を保持したまま order_complete_n.jsp に処理を引き渡す
            request.getRequestDispatcher("/order_complete_n.jsp").forward(request, response);
            
            LOGGER.info(" - order_complete_n.jsp にフォワードしました。");

        } catch (Exception e) {
            LOGGER.severe("ファイルの処理中またはデータの取得中にエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
            // エラーページにリダイレクト
            if (!response.isCommitted()) {
                response.sendRedirect("error.jsp?message=order_error"); 
            }
        }
    }
    
    /**
     * Partを保存し、保存したファイル名の絶対パスを返すヘルパーメソッド
     */
    private String processAndSavePart(Part part, String savePath, String timestamp, String suffix) throws IOException {
        if (part != null && part.getSize() > 0) {
            String originalFileName = extractFileName(part);
            String sanitizedFileName = sanitizeFileName(originalFileName);
            String savedFileName = "ORDER_" + timestamp + suffix + sanitizedFileName;
            
            String absolutePath = savePath + File.separator + savedFileName;
            
            part.write(absolutePath);
            LOGGER.info("  - 画像を保存しました: " + absolutePath);
            return absolutePath; // 絶対パスを返す
        }
        return ""; // ファイルがアップロードされなかった場合は空文字列を返す
    }

    // Partからファイル名を取り出すヘルパーメソッド (変更なし)
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp != null) {
            for (String s : contentDisp.split(";")) {
                if (s.trim().startsWith("filename")) {
                    return s.substring(s.indexOf("=") + 2, s.length() - 1);
                }
            }
        }
        return "unknown_file";
    }
    
    // ファイル名のサニタイズ（無害化）メソッド (変更なし)
    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_") 
                        .replaceAll("\\.\\.", "_"); 
    }
}