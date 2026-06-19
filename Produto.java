public class Produto {
    private int id;
    private String nome;
    private String descricao;
    private double preco;
    private String categoria;
    private int stock;

    // Construtor Completo (Usado pelo DAO ao ler do banco)
    public Produto(int id, String nome, String descricao, double preco, String categoria, int stock) {
        if(preco < 0 || stock < 0){
            throw new IllegalArgumentException("Valores inválidos.");
        }
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.categoria = categoria;
        this.stock = stock;
    }

    // Construtor sem ID (Usado ao criar um novo produto antes de salvar)
    public Produto(String nome, String descricao, double preco, String categoria, int stock) {
        this(0, nome, descricao, preco, categoria, stock);
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public double getPreco() { return preco; }
    public String getCategoria() { return categoria; }
    public int getStock() { return stock; }

    @Override
    public String toString() {
        return nome + " | Preço: " + preco + " | Stock: " + stock;
    }
    
    public double calcularValorStock(){
        return preco * stock;
    }

    public void adicionarStock(int quantidade) {
        if(quantidade > 0){
            stock += quantidade;
        }
    }

    public boolean removerStock(int quantidade){
        if(quantidade <= stock){
            stock -= quantidade;
            return true;
        }
        return false;
    }
}