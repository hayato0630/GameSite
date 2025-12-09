document.addEventListener('DOMContentLoaded', () => {
    // すべての「購入する」ボタンに付与したクラスを取得
    const purchaseLinks = document.querySelectorAll('.purchase-link');

    purchaseLinks.forEach(link => {
        link.addEventListener('click', (event) => {
            event.preventDefault(); // リンクのデフォルト動作（単なるページ遷移）を停止

            // クリックされたボタンの親要素（.item-card）からアイテムデータを取得
            const card = event.currentTarget.closest('.item-card');
            if (!card) return;

            const itemId = card.dataset.id;
            const itemName = card.dataset.name;
            const itemPrice = card.dataset.price;

            // 取得したデータをURLのクエリパラメータとして整形
            // 例: ?id=stone_l&name=魔法石パック%20(特大)&price=5800
            const queryString = new URLSearchParams({
                id: itemId,
                name: itemName,
                price: itemPrice
            }).toString();

            // checkout.htmlへパラメータ付きで遷移
            window.location.href = `checkout_n.jsp?${queryString}`;
        });
    });
});