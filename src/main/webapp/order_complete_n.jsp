<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%!
    // JSP宣言：注文ID生成用のヘルパーメソッド
    public String generateOrderID() {
        // ID生成ロジックは変更なし
        return "TXN" + (int)(Math.random() * 900000 + 100000);
    }
%>
<%
    // =======================================================
    // ★★★ 修正箇所 3: Servletから渡された属性の取得 ★★★
    // =======================================================
    // Servletがrequest.setAttributeで設定した値をrequest.getAttributeで取得する
    String itemName = (String)request.getAttribute("itemName");
    String itemPriceStr = (String)request.getAttribute("itemPrice"); // 価格は文字列で取得
    String paymentMethod = (String)request.getAttribute("paymentMethod");
    
    // JSPスクリプトレット：注文日時をサーバー側で取得・整形 (変更なし)
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    String orderTime = sdf.format(new Date());
    
    // itemNameが存在しない場合のフォールバック (変更なし)
    if (itemName == null || itemName.isEmpty()) {
        itemName = "情報が失われました";
        itemPriceStr = "0";
    }
    if (paymentMethod == null || paymentMethod.isEmpty()) {
        paymentMethod = "情報が渡されていません（処理エラーの可能性があります）";
    }
    
    // 価格のフォーマット処理 (変更なし)
    long itemPrice = 0;
    try {
        itemPrice = Long.parseLong(itemPriceStr);
    } catch (Exception e) {
        // 価格変換エラー時の処理
    }
    String formattedPrice = String.format("%,d", itemPrice);

    // 注文IDの生成（またはServletから渡されたものを使用）(変更なし)
    String orderID = generateOrderID(); 

%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ご注文確定 | 【広島情報龍伝記】</title>
    <link rel="stylesheet" href="dezain_n.css">
</head>
<body>

    <header class="header">
        </header>

    <main class="main-content">
        <section class="section complete-message">
            <h1>✅ ご注文が確定しました。ありがとうございます！</h1>
            <p class="sub-message">ご購入いただいたアイテムは、ゲーム内に即時反映されます。</p>
            <p>引き続き【広島情報龍伝記】をお楽しみください！</p>
        </section>

        <section class="section order-details">
            <h2>ご注文内容詳細</h2>
            
			<div class="detail-box">
                <p><strong>注文日時:</strong> <span id="order-date"><%= orderTime %></span></p> 
                <p><strong>注文ID:</strong> <span class="order-id" id="order-id-display"><%= orderID %></span></p> 
                <p><strong>決済方法:</strong> <span id="payment-method-display"><%= paymentMethod %></span></p> 
            </div>
            
            <table class="item-table">
                <thead>
                    <tr>
                        <th>商品名</th>
                        <th class="align-right">単価（税込）</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td id="item-name-display"><%= itemName %></td> 
                        <td class="align-right" id="item-price-display">¥ <%= formattedPrice %></td>
                    </tr>
                </tbody>
                <tfoot>
                    <tr>
                        <td class="total-label">合計金額（税込）</td>
                        <td class="align-right total-price" id="total-price-display">¥ <%= formattedPrice %></td>
                    </tr>
                </tfoot>
            </table>

            <div class="next-step">
                <p>この画面は自動で保存されません。必要な場合はスクリーンショットを保存してください。</p>
                <a href="store_n.html" class="btn-main">ストアへ戻る</a>
                <a href="GeemuKosiki_n.html" class="btn-secondary">トップページへ戻る</a>
            </div>
        </section>
    </main>
	
	<footer class="footer">
        </footer>
	
    </body>
</html>