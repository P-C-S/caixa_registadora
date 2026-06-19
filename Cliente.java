public class Cliente {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private String endereco;
    private String telefone;
    private boolean pedido = false; 

    public Cliente(int id, String nome, String email, String senha, String endereco, String telefone) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.endereco = endereco;
        this.telefone = telefone;
    }

    public Cliente(String nome, String email, String senha, String endereco, String telefone) {
        this(0, nome, email, senha, endereco, telefone);
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public String getEndereco() { return endereco; }
    public String getTelefone() { return telefone; }

    public void setpedido(boolean pedido) {    
        this.pedido = pedido;
    }
   
    public boolean haspedido() {
        return pedido;
    }

    @Override
    public String toString() {
        return "Cliente: " + nome + " | ID: " + id;
    }
}