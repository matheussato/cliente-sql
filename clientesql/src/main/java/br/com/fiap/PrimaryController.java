package br.com.fiap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PrimaryController {

    @FXML TextField textFieldUsuario;
    @FXML PasswordField passwordFieldSenha;
    @FXML TextField textFieldURL;
    @FXML TextArea textAreaSQL;
    @FXML Label status;
    @FXML ListView<String> historico;
    @FXML TableView<ArrayList<String>> tabela;


        public void executar(){
            //cath user,pass and URL
            String usuario = textFieldUsuario.getText();
            String senha = passwordFieldSenha.getText();
            String url = textFieldURL.getText();
            
            //conectar  no BD
            try {
                //conectar  no BD
                Connection con = DriverManager.getConnection(url,usuario,senha);

                //executar SQL
                String sql = sanitizar(textAreaSQL.getText());
                var comando = con.prepareStatement(sql);
                var resultado = comando.executeQuery();

                // se for select tem que carregar tabela
                if(sql.toUpperCase().startsWith("SELECT")) carregarDadosNaTabela(resultado);

                mostrarMensagem("Comando executado:  " + sql);
                historico.getItems().add(sql);

                //fechar conexao
                con.close();
            } catch (SQLException e) {
                mostrarMensagem("Erro de SQL." + e.getMessage());
            }
            
        }

        private void carregarDadosNaTabela(ResultSet resultado) throws SQLException {
            int columnCount = resultado.getMetaData().getColumnCount();
            tabela.getColumns().removeAll(tabela.getColumns());

            for(int i = 1; i <= columnCount; i++){
                var columnName = resultado.getMetaData().getColumnLabel(i);
                TableColumn<ArrayList<String>,String> tableColumn = new TableColumn<>(columnName);
                tableColumn.setCellFactory(new CallbackImp(i-1));
                tabela.getColumns().add(tableColumn);

            }
            tabela.getItems().clear();
            while(resultado.next()){
                var lista = new ArrayList<String>();
                for(int i = 1; i<= columnCount; i++){
                    lista.add(resultado.getString(1));
                }
                tabela.getItems().add(lista);

                
            }
        }

        private String sanitizar(String sql){
            return sql.replaceAll(";", "" ).replaceAll("\"", "'" );
        }

        private void mostrarMensagem(String string) {
            status.setText(string);
        }

        public void carregarHistorico(){
            String comando = (String) historico.getSelectionModel().getSelectedItem();
            textAreaSQL.setText(comando);
        }
}
