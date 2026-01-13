<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ご注文情報の入力</title>
<link rel="stylesheet" href="dezain_n.css"> 
<style>
/* CSSの例: 表示/非表示を切り替えるためのスタイル */
.hidden {
    display: none;
}
.card-details, .bank-details {
    padding: 15px;
    border: 1px solid #ccc;
    border-radius: 5px;
    margin-top: 10px;
}
</style>
</head>
<body>
    <header class="header">
        </header>

	<main class="main-content" style="padding-top: 120px;">
        <section class="section checkout">
            <h2>ご注文情報の入力</h2>
            <!-- 
                【CRITICAL FIX】: フォームアクションをJSPタグを使用して絶対パスに修正。
                これにより、コンテキストルートが /Gamesite であっても /hoge であっても、
                /Gamesite/GameServlet や /hoge/GameServlet のように正しくパスが解決されます。
                このパスが GameServlet の web.xml マッピング /GameServlet とマッチします。
            -->
            <form action="GameServlet" method="POST" class="purchase-form" enctype="multipart/form-data">                
                <div class="form-section order-summary">
                    <h3>ご注文内容</h3>
                        <p>アイテム名: <span id="selected-item-name">ロード中...</span></p> 
                        <p>単価: <span id="selected-item-price">¥ 0</span></p>
                        <hr>
                        <p class="total">合計金額: <span id="total-amount">¥ 0 (税込)</span></p>
                        
                        <input type="hidden" id="item-id-input" name="item_id" value=""> 
                        <input type="hidden" id="item-name-hidden" value="">
                        <input type="hidden" id="item-price-hidden" value="">
                    </div>

                <div class="form-section payment-info">
                    <h3>お支払い情報</h3>
                    <label>
                        <input type="radio" id="radio-credit" name="payment_method" value="クレジットカード" required checked> 
                        クレジットカード
                    </label>
                    <div id="card-details" class="card-details">
                        <p>カード情報の入力欄...</p>
                        <input type="text" name="number" placeholder="カード番号" required>
                        <input type="text" name="day"placeholder="有効期限 (MM/YY)" required>
                        <input type="text" name="code" placeholder="セキュリティコード" required>
                    </div>

                    
                    <div id="bank-details" class="bank-details hidden">
                        <p>ご注文後、振込先口座情報をメールでお知らせします。</p>
                        <p>※振込手数料はお客様のご負担となります。</p>
                    </div>
                    <div class="form-section upload-section">
    					<div class="form-section upload-section">
   						 <h3>画像のアップロード</h3>
						<p>本人確認のため、運転免許証もしくはマイナンバーカードの画像をアップロードしてください。</p>
    					<p>表面の画像をアップロードしてください。</p>
    					<input type="file" name="upload_image_front" accept="image/*" required>

   						 <p style="margin-top: 15px;">裏面の画像をアップロードしてください。</p>
    						<input type="file" name="upload_image_back" accept="image/*" required>
						</div>
					</div>
                </div>
                
                
                <br><button type="submit" class="btn-checkout">支払う</button>
            </form>
            
            <ul class="note-list">
                <li>※ご購入いただいたアイテムは、ご利用のプラットフォームのみにてご利用可能です。</li>
                <li>※購入完了後の返品・交換は、原則として受け付けておりません。</li>
                <li>特定商取引法に基づく表記をご確認ください。</li>
            </ul>
        </section>
	</main>

	<footer class="footer">
        </footer>
	    
    <script src="checkout_n.js"></script>

</body>
</html>