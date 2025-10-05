# NourishNet

NourishNet é uma aplicação backend desenvolvida em Java com Spring Boot, voltada para gestão e distribuição de doações alimentares.

## Funcionalidades
- Cadastro e autenticação de usuários
- Gerenciamento de doações
- Controle de entregas
- Alertas e notificações
- Relatórios e logs de eventos

## Tecnologias Utilizadas
- Java 17
- Spring Boot
- Maven
- Firebase
- Oracle Autonomous Database

## Estrutura do Projeto
```
src/
  main/
    java/
      backend/
        nourishnet/
          adapter/     
          api/        
          config/      
          domain/       
          dto/         
          jobs/         
          repository/   
          security/      
          service/      
          shared/         
          support/        
    resources/
      application.properties
      logback-spring.xml
      db/
        migration/
      static/
      templates/
  test/
    java/
      backend/
        nourishnet/
          NourishnetApplicationTests.java
```

## Endpoints REST

Abaixo estão os principais endpoints expostos pela API NourishNet:

- `/api` - basepath

### Autenticação e Usuários
- `POST /auth/signup` — Cadastro de usuário
- `POST /auth/login` — Login de usuário
- `POST /users` — Criação de usuário (admin)
- `GET /users/{id}` — Consulta de usuário por ID
- `PUT /users/{id}` — Atualização de usuário por ID

### Doações
- `POST /donations` — Criar doação
- `GET /donations` — Listar doações
- `GET /donations/{id}` — Detalhes de uma doação
- `PUT /donations/{id}` — Atualizar doação

### Entregas
- `POST /deliveries` — Registrar entrega
- `GET /deliveries` — Listar entregas
- `GET /deliveries/{id}` — Detalhes da entrega
- `PUT /deliveries/{id}/proof` — Enviar comprovante de entrega

### Alertas
- `GET /alerts` — Listar alertas
- `GET /alerts/count` — Contagem de alertas

### Match
- `POST /matches/{id}/accept` — Aceitar match de doação
- `GET /matches` — Listar matches

### Feed
- `GET /feed` — Feed de doações disponíveis

### Admin
- `GET /admin/msg` — Mensagem administrativa

### Logs de Eventos
- `GET /event-logs` — Listar logs de eventos

## Como Executar
1. Certifique-se de ter o Java 17+ e Maven instalados.
2. Configure o arquivo `application.properties` conforme necessário.
3. Execute o comando:
   ```bash
   ./mvnw spring-boot:run
   ```

## Testes
Para rodar os testes automatizados:
```bash
./mvnw test
```
