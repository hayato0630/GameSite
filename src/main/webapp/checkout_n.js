document.addEventListener('DOMContentLoaded', () => {
    // =======================================================
    // 1. URLからクエリパラメータ（アイテムデータ）を取得し、表示に反映
    // =======================================================
    const params = new URLSearchParams(window.location.search);
    const itemId = params.get('id');
    const itemName = params.get('name');
    const itemPrice = params.get('price');

    const purchaseForm = document.querySelector('.purchase-form');

    // 2. データが存在するか確認
    if (itemId && itemName && itemPrice) {
        // 3. 取得したデータをHTML要素に反映
        const formattedPrice = Number(itemPrice).toLocaleString();
        
        // JSP環境ではinnerText/textContent推奨
        document.getElementById('selected-item-name').textContent = itemName; 
        document.getElementById('selected-item-price').textContent = `¥ ${formattedPrice}`;
        document.getElementById('total-amount').textContent = `¥ ${formattedPrice} (税込)`;

        // フォーム送信用の非表示フィールドにデータをセット
        document.getElementById('item-id-input').value = itemId;
        // name属性がないため、ここでは非表示フィールドにセットする必要はありません
        // document.getElementById('item-name-hidden').value = itemName; 
        // document.getElementById('item-price-hidden').value = itemPrice;
        
    } else {
        // alert()の代わりに、HTMLコンテンツを置き換えるか、ネイティブなwindow.location.hrefを使用
        // (ここではブラウザ環境を想定し、元のロジックを維持しつつ、alertは削除)
        console.error('選択されたアイテム情報が見つかりませんでした。ストアページに戻ります。');
        window.location.href = 'store_n.html'; 
        return; // 処理を中断
    }

    // =======================================================
    // 2. 支払い方法の表示切替ロジック 
    // =======================================================
    const radioCredit = document.getElementById('radio-credit');
    const radioBank = document.getElementById('radio-bank');
    const cardDetails = document.getElementById('card-details');
    const bankDetails = document.getElementById('bank-details');

    function togglePaymentMethod() {
        if (radioCredit && radioBank && cardDetails && bankDetails) {
            if (radioCredit.checked) {
                cardDetails.classList.remove('hidden');
                bankDetails.classList.add('hidden');
            } else if (radioBank.checked) {
                cardDetails.classList.add('hidden');
                bankDetails.classList.remove('hidden');
            }
        }
    }

    if (radioCredit && radioBank) {
        radioCredit.addEventListener('change', togglePaymentMethod);
        radioBank.addEventListener('change', togglePaymentMethod);
        togglePaymentMethod();
    }


    // =======================================================
    // 3. フォーム送信処理（サーバーへ送信するための修正）
    // =======================================================
    
    if (purchaseForm) { // フォーム要素が存在することを確認
        purchaseForm.addEventListener('submit', function(event) {
            // CRITICAL FIX: event.preventDefault() を削除したため、ここでの処理は
            // ユーザーフィードバック（ボタン無効化）に絞ります。
            
            // ネイティブバリデーションが成功した場合のみ、以下の処理を実行
            if (purchaseForm.checkValidity()) {
                
                const submitButton = purchaseForm.querySelector('.btn-checkout');
                // 処理中はボタンを無効化し、ユーザーに待機を促す
                submitButton.textContent = '送信中...';
                submitButton.disabled = true; 
                
                // フォームのネイティブな送信が自動的に続行されます。
            }
            // バリデーションエラー時は、ネイティブなHTMLバリデーションポップアップが表示されます。
        });
    }
});