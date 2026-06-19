public class ItemPedido {
//----------------------------------------------------------------------------------
    private Produto produto;
    private int quantidade;
//-----------------------------CONSTRUTOR-------------------------------------------
    public ItemPedido(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }
//------------------------------METODOS--------------------------------------------
    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double subtotal(){
        return produto.getPreco() * quantidade;
    }
//----------------------------------------------------------------------------------
}
