import java.awt.*;
import java.sql.Connection;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TelaPrincipal extends JFrame {

    private JTabbedPane painelAbas;
    private ProdutoDAO produtoDAO;
    private ClienteDAO clienteDAO;

    // Componentes de controlo de estado local do carrinho
    private Pedido pedidoAtual;
    private DefaultTableModel modeloCarrinho;
    private JLabel lblTotal;

    public TelaPrincipal() {
        // Inicializa a conexão com a Base de Dados de forma segura
        try {
            Connection conexao = ConexaoBD.obterConexao();
            this.produtoDAO = new ProdutoDAO(conexao);
            this.clienteDAO = new ClienteDAO(conexao);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro de Conexão à Base de Dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

        // Cria um cliente genérico temporário para o Carrinho de compras
        Cliente clientePadrao = new Cliente(1, "Consumidor Final", "anonimo@loja.com", "123", "Mindelo", "000000");
        this.pedidoAtual = new Pedido(clientePadrao);

        setTitle("Sistema de Gestão de Loja - UniMindelo");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        painelAbas = new JTabbedPane();
        painelAbas.addTab("Produtos", criarPainelProdutos());
        painelAbas.addTab("Usuários / Clientes", criarPainelUsuarios());
        painelAbas.addTab("Carrinho & Pedidos", criarPainelPedidos());
        painelAbas.addTab("Relatórios Vendas", criarPainelRelatorios());

        add(painelAbas, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rodape.setBorder(BorderFactory.createEtchedBorder());
        rodape.add(new JLabel("Universidade do Mindelo - Sapientia Ars Vivendi"));
        add(rodape, BorderLayout.SOUTH);
    }

    // 1. ABA DE PRODUTOS (Totalmente Integrada com a BD)
    private JPanel criarPainelProdutos() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new GridLayout(7, 2, 5, 5));
        formulario.setBorder(BorderFactory.createTitledBorder("Dados do Produto"));
        
        JTextField txtId = new JTextField();
        txtId.setEditable(false); // ID é auto-incremento na BD
        txtId.setBackground(Color.LIGHT_GRAY);
        JTextField txtNome = new JTextField();
        JTextField txtDescricao = new JTextField();
        JTextField txtPreco = new JTextField();
        JTextField txtCategoria = new JTextField();
        JTextField txtEstoque = new JTextField();

        formulario.add(new JLabel("ID (Seleção):")); formulario.add(txtId);
        formulario.add(new JLabel("Nome:")); formulario.add(txtNome);
        formulario.add(new JLabel("Descrição:")); formulario.add(txtDescricao);
        formulario.add(new JLabel("Preço:")); formulario.add(txtPreco);
        formulario.add(new JLabel("Categoria:")); formulario.add(txtCategoria);
        formulario.add(new JLabel("Estoque:")); formulario.add(txtEstoque);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnSalvar = new JButton("Salvar / Novo");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnExcluir = new JButton("Excluir");
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnExcluir);
        formulario.add(painelBotoes);

        String[] colunas = {"ID", "Nome", "Descrição", "Preço", "Categoria", "Estoque"};
        DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(modeloTabela);
        JScrollPane scrollTabela = new JScrollPane(tabela);

        // Método interno para recarregar os dados da BD na tabela da UI
        Runnable recarregarProdutos = () -> {
            modeloTabela.setRowCount(0);
            if (produtoDAO != null) {
                List<Produto> lista = produtoDAO.listarProdutos();
                for (Produto p : lista) {
                    modeloTabela.addRow(new Object[]{p.getId(), p.getNome(), p.getDescricao(), p.getPreco(), p.getCategoria(), p.getStock()});
                }
            }
        };
        recarregarProdutos.run();

        // Evento ao clicar numa linha da tabela (Preenche os campos de texto)
        tabela.getSelectionModel().addListSelectionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha != -1) {
                txtId.setText(modeloTabela.getValueAt(linha, 0).toString());
                txtNome.setText(modeloTabela.getValueAt(linha, 1).toString());
                txtDescricao.setText(modeloTabela.getValueAt(linha, 2).toString());
                txtPreco.setText(modeloTabela.getValueAt(linha, 3).toString());
                txtCategoria.setText(modeloTabela.getValueAt(linha, 4).toString());
                txtEstoque.setText(modeloTabela.getValueAt(linha, 5).toString());
            }
        });

        // Ação de Salvar
        btnSalvar.addActionListener(e -> {
            try {
                Produto p = new Produto(txtNome.getText(), txtDescricao.getText(), Double.parseDouble(txtPreco.getText()), txtCategoria.getText(), Integer.parseInt(txtEstoque.getText()));
                produtoDAO.salvar(p);
                recarregarProdutos.run();
                JOptionPane.showMessageDialog(painel, "Produto inserido com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(painel, "Erro nos dados introduzidos: " + ex.getMessage());
            }
        });

        // Ação de Atualizar
        btnAtualizar.addActionListener(e -> {
            try {
                Produto p = new Produto(Integer.parseInt(txtId.getText()), txtNome.getText(), txtDescricao.getText(), Double.parseDouble(txtPreco.getText()), txtCategoria.getText(), Integer.parseInt(txtEstoque.getText()));
                produtoDAO.atualizarProduto(p);
                recarregarProdutos.run();
                JOptionPane.showMessageDialog(painel, "Produto atualizado com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(painel, "Selecione um produto e valide os campos!");
            }
        });

        // Ação de Excluir
        btnExcluir.addActionListener(e -> {
            try {
                produtoDAO.deletarProduto(Integer.parseInt(txtId.getText()));
                recarregarProdutos.run();
                JOptionPane.showMessageDialog(painel, "Produto removido com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(painel, "Selecione um produto para excluir!");
            }
        });

        painel.add(formulario, BorderLayout.WEST);
        painel.add(scrollTabela, BorderLayout.CENTER);
        return painel;
    }

    // 2. ABA DE USUÁRIOS (Clientes)
    private JPanel criarPainelUsuarios() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new GridLayout(6, 2, 5, 5));
        formulario.setBorder(BorderFactory.createTitledBorder("Dados do Usuário / Cliente"));

        JTextField txtNome = new JTextField();
        JTextField txtEmail = new JTextField();
        JPasswordField txtSenha = new JPasswordField();
        JTextField txtEndereco = new JTextField();
        JTextField txtTelefone = new JTextField();

        formulario.add(new JLabel("Nome:")); formulario.add(txtNome);
        formulario.add(new JLabel("E-mail:")); formulario.add(txtEmail);
        formulario.add(new JLabel("Senha:")); formulario.add(txtSenha);
        formulario.add(new JLabel("Endereço:")); formulario.add(txtEndereco);
        formulario.add(new JLabel("Telefone:")); formulario.add(txtTelefone);

        JButton btnCadastrar = new JButton("Cadastrar Usuário");
        formulario.add(new JLabel("")); formulario.add(btnCadastrar);

        String[] colunas = {"ID", "Nome", "E-mail", "Endereço", "Telefone"};
        DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(modeloTabela);
        JScrollPane scroll = new JScrollPane(tabela);

        Runnable recarregarClientes = () -> {
            modeloTabela.setRowCount(0);
            if (clienteDAO != null) {
                List<Cliente> lista = clienteDAO.listarClientes();
                for (Cliente c : lista) {
                    modeloTabela.addRow(new Object[]{c.getId(), c.getNome(), c.getEmail(), c.getEndereco(), c.getTelefone()});
                }
            }
        };
        recarregarClientes.run();

        btnCadastrar.addActionListener(e -> {
            try {
                Cliente c = new Cliente(txtNome.getText(), txtEmail.getText(), new String(txtSenha.getPassword()), txtEndereco.getText(), txtTelefone.getText());
                clienteDAO.salvar(c);
                recarregarClientes.run();
                JOptionPane.showMessageDialog(painel, "Cliente registado com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(painel, "Erro ao guardar utilizador.");
            }
        });

        painel.add(formulario, BorderLayout.WEST);
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    // 3. ABA DE CARRINHO E PEDIDOS (Dinamismo Real)
    private JPanel criarPainelPedidos() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        painelTopo.setBorder(BorderFactory.createTitledBorder("Adicionar ao Carrinho (Com base nos IDs do Banco)"));
        
        painelTopo.add(new JLabel("Produto (ID):"));
        JTextField txtIdProd = new JTextField(5);
        painelTopo.add(txtIdProd);
        
        painelTopo.add(new JLabel("Quantidade:"));
        JTextField txtQtd = new JTextField(5);
        painelTopo.add(txtQtd);

        JButton btnAdicionar = new JButton("Adicionar Item");
        painelTopo.add(btnAdicionar);

        String[] colunas = {"Item N°", "Produto", "Qtd", "Preço Unitário", "Subtotal"};
        modeloCarrinho = new DefaultTableModel(colunas, 0);
        JTable tabelaCarrinho = new JTable(modeloCarrinho);
        JScrollPane scrollCarrinho = new JScrollPane(tabelaCarrinho);

        JPanel painelInferior = new JPanel(new BorderLayout());
        lblTotal = new JLabel("TOTAL DO PEDIDO: 0.00 CVE  ", JLabel.RIGHT);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        
        JButton btnFinalizar = new JButton("Finalizar Compra (Criar Pedido)");
        btnFinalizar.setBackground(new Color(46, 139, 87));
        btnFinalizar.setForeground(Color.WHITE);
        btnFinalizar.setFont(new Font("Arial", Font.BOLD, 14));

        // Evento para ler a BD por ID, validar stock e adicionar ao pedido em memória
        btnAdicionar.addActionListener(e -> {
            try {
                int idBusca = Integer.parseInt(txtIdProd.getText());
                int qtd = Integer.parseInt(txtQtd.getText());

                // Procura o produto real na lista da BD
                Produto alvo = null;
                for (Produto p : produtoDAO.listarProdutos()) {
                    if (p.getId() == idBusca) {
                        alvo = p;
                        break;
                    }
                }

                if (alvo != null) {
                    if (alvo.getStock() >= qtd) {
                        pedidoAtual.retirarProduto(alvo, qtd); // Executa as regras de negócio
                        
                        // Atualiza na base de dados o stock abatido
                        produtoDAO.atualizarProduto(alvo);

                        // Atualiza o painel visual do carrinho
                        modeloCarrinho.setRowCount(0);
                        int contador = 1;
                        for (ItemPedido item : pedidoAtual.getItens()) {
                            modeloCarrinho.addRow(new Object[]{
                                contador++, 
                                item.getProduto().getNome(), 
                                item.getQuantidade(), 
                                item.getProduto().getPreco(), 
                                item.subtotal()
                            });
                        }
                        lblTotal.setText("TOTAL DO PEDIDO: " + pedidoAtual.calcularTotal() + " CVE  ");
                    } else {
                        JOptionPane.showMessageDialog(painel, "Stock insuficiente! Stock atual: " + alvo.getStock());
                    }
                } else {
                    JOptionPane.showMessageDialog(painel, "Produto com ID " + idBusca + " não foi encontrado.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(painel, "Insira valores válidos de ID e Quantidade.");
            }
        });

        // Finaliza o fluxo limpando o carrinho para a próxima venda
        btnFinalizar.addActionListener(e -> {
            if(pedidoAtual.getItens().isEmpty()) {
                JOptionPane.showMessageDialog(painel, "O carrinho está vazio!");
                return;
            }
            JOptionPane.showMessageDialog(painel, "Pedido Finalizado com sucesso!\nTotal Cobrado: " + pedidoAtual.calcularTotal() + " CVE");
            // Reinicia o objeto de pedido
            Cliente cTemp = new Cliente(1, "Consumidor Final", "", "", "", "");
            pedidoAtual = new Pedido(cTemp);
            modeloCarrinho.setRowCount(0);
            lblTotal.setText("TOTAL DO PEDIDO: 0.00 CVE  ");
        });

        painel.add(painelTopo, BorderLayout.NORTH);
        painel.add(scrollCarrinho, BorderLayout.CENTER);
        painel.add(painelInferior, BorderLayout.SOUTH);
        painelInferior.add(lblTotal, BorderLayout.NORTH);
        painelInferior.add(btnFinalizar, BorderLayout.SOUTH);

        return painel;
    }

    // 4. ABA DE RELATÓRIOS (Descritivos em tempo real)
    private JPanel criarPainelRelatorios() {
        JPanel painel = new JPanel(new BorderLayout(15, 15));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel painelBotoes = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton btnStock = new JButton("Relatório de Inventário Atual");
        JButton btnUsuariosAtivos = new JButton("Lista de Clientes Registados");
        painelBotoes.add(btnStock);
        painelBotoes.add(btnUsuariosAtivos);

        JTextArea areaRelatorio = new JTextArea();
        areaRelatorio.setEditable(false);
        areaRelatorio.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaRelatorio.setText("=== Clique em um dos botões acima para gerar o relatório descritivo ===");
        JScrollPane scroll = new JScrollPane(areaRelatorio);

        // Relatório Dinâmico de Inventário vindo direto da BD
        btnStock.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            sb.append("==================================================\n");
            sb.append("       RELATÓRIO DE INVENTÁRIO - LOJA UNIMINDELO  \n");
            sb.append("==================================================\n");
            double totalFinanceiro = 0;
            for(Produto p : produtoDAO.listarProdutos()) {
                sb.append(String.format("ID: %d | %-20s | Preço: %.2f | Stock: %d\n", p.getId(), p.getNome(), p.getPreco(), p.getStock()));
                totalFinanceiro += p.calcularValorStock();
            }
            sb.append("--------------------------------------------------\n");
            sb.append(String.format("Valor Total de Mercadoria em Stock: %.2f CVE\n", totalFinanceiro));
            sb.append("==================================================");
            areaRelatorio.setText(sb.toString());
        });

        // Relatório Dinâmico de Utilizadores cadastrados
        btnUsuariosAtivos.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            sb.append("==================================================\n");
            sb.append("       CLIENTES REGISTADOS NO SISTEMA             \n");
            sb.append("==================================================\n");
            for(Cliente c : clienteDAO.listarClientes()) {
                sb.append(String.format("ID: %d | Nome: %-20s | Endereço: %s\n", c.getId(), c.getNome(), c.getEndereco()));
            }
            sb.append("==================================================");
            areaRelatorio.setText(sb.toString());
        });

        painel.add(painelBotoes, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }
}