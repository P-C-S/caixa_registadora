import java.util.ArrayList;

public class Pedido {
    private Cliente cliente; 
    private ArrayList<ItemPedido> itens;
    
    public Pedido(Cliente cliente) {
        this.cliente = cliente;
        this.itens = new ArrayList<>(); 
    }

    public ArrayList<ItemPedido> getItens() {
        return itens;
    }

    public void retirarProduto(Produto produto, int quantidade){
        if(quantidade <= 0){
            System.out.println("Quantidade inválida.");
            return;
        }
        if(produto.getStock() < quantidade){
            System.out.println("Stock insuficiente.");
            return;
        }
        produto.removerStock(quantidade);
        ItemPedido item = new ItemPedido(produto, quantidade);
        itens.add(item);
    }

    public double calcularTotal(){
        double total = 0;
        for(ItemPedido item : itens){
            total += item.subtotal();
        }
        return total;
    }

    public void exibirResumo(){
        System.out.println("\n===== PEDIDO =====");
        System.out.println("Cliente: " + cliente.getNome()); 
        System.out.println("id" + cliente.getId()); 
        for(ItemPedido item : itens){
            Produto p = item.getProduto();
            System.out.println(p.getNome() + " | Quantidade: "+ item.getQuantidade() + " | Subtotal: "+ item.subtotal());
        }
        System.out.println("====================");
        System.out.println("TOTAL: " + calcularTotal());
    }
}