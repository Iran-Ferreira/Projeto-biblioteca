# Projeto Final POO
## Como rodar o projeto 
## Passo 1: Compilar
javac -d bin --source-path src src/com/biblioteca/Main.java
## Passo 2: Executar
java -cp bin com.biblioteca.Main  
Adendo: Nesse projeto fiz 2 arquivos o Main e o Main2.   
Main: tem o menu interativo  
Main2: Já está com os valores de teste

## Caso de teste 
Ao rodar o programa, você verá o Menu Principal. O sistema já inicia com um Administrador ("Admin Padrão") e dois livros ("Java 21" e "Clean Code") para facilitar.

1. Cadastrando um Usuário (Maria)
    1. No menu principal, digite 2 (Gerenciar Usuários) e aperte Enter.
    2. No submenu, digite 1 (Cadastrar Novo Leitor).
    3. O sistema pedirá o nome. Digite: Maria Souza.  
    4. O sistema confirmará o cadastro e mostrará o ID.  
    5. Volte ao menu principal.
2. Realizando um Empréstimo
   Agora a Maria vai pegar o livro "Clean Code".
    1. No menu principal, digite 3 (Gerenciar Empréstimos).
    2. Digite 1 (Realizar Empréstimo).
    3. Selecionar Leitor: O sistema mostrará uma lista. Provavelmente a Maria será a opção 1. Digite 1.
    4. Selecionar Livro: O sistema listará os livros disponíveis. Vamos supor que "Clean Code" seja a opção 2. Digite 2.
    5. Definir Prazo: O sistema pergunta quantos dias ela ficará com o livro. Digite 7 (uma semana).
    6. O empréstimo foi registrado.
3. Verificando o Status (Consultar Acervo)
    1. Volte ao menu principal.
    2. Digite 1 (Gerenciar Acervo).
    3. Digite 3 (Listar Todos os Livros).
    4. Observe: O livro "Clean Code" agora aparecerá escrito como (Emprestado), enquanto o "Java 21" continuará (Disponível). Isso mostra que o encapsulamento e a mudança de estado do objeto Livro funcionaram.
4. Devolvendo o Livro (Com Multa Simulada)
   A Maria vai devolver o livro.
    1. Volte ao menu principal.
    2. Vá em 3 (Gerenciar Empréstimos).
    3. Escolha 2 (Realizar Devolução).
    4. O sistema mostrará o empréstimo ativo da Maria. Selecione o número correspondente (ex: 1).
    5. Resultado:
       6. O sistema dirá: DEVOLUÇÃO REALIZADA: 'Clean Code'.
       7. Como a data de devolução no código é LocalDate.now() (hoje) e o prazo era daqui a 7 dias, o sistema dirá: Devolução dentro do prazo. Sem multas.