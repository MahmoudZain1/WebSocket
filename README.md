# WebSocket One-to-One Chat Application

A real-time one-to-one chat application built with Spring Boot WebSocket, STOMP, and SockJS. This application allows users to engage in private conversations with one another in a seamless and interactive environment.


## ✨ Features

- **Real-time messaging** - Send and receive messages instantly
- **User presence** - See who's online and available to chat
- **Private conversations** - Engage in one-to-one conversations with other users
- **Join/Leave notifications** - Get notified when users join or leave
- **Responsive design** - Works on desktop and mobile devices
- **Arabic language support** - Fully supports Arabic text and RTL layout

## 🛠️ Technology Stack

- **Backend:**
  - Java 23
  - Spring Boot 
  - Spring WebSocket
  - STOMP Protocol

- **Frontend:**
  - HTML
  - CSS
  - JavaScript
  - SockJS
  - STOMP.js
    
## 🏗️ Architecture

This application follows a client-server architecture using WebSockets:

1. **Client-side**: Browser-based interface that connects to the server using SockJS and communicates via STOMP protocol
2. **Server-side**: Spring Boot application that handles WebSocket connections, user session management, and message routing

Messages are exchanged in JSON format with the following structure:
```json
{
  "sender": "username",
  "recipient": "targetUsername",
  "content": "message content",
  "type": "MESSAGE_TYPE"
}
```

## 📋 Prerequisites

- Java 11 or higher
- Maven 3.6.0 or higher
- Git

## 📥 Installation

Clone the repository to your local machine:

```bash
# Create a directory for the project
mkdir websocket-chat
cd websocket-chat

# Clone the repository
git clone https://github.com/MahmoudZain1/WebSocket.git

# Navigate to the project directory
cd websocket-chat
```

##  Running the Application

1. **Build the application with Maven:**

```bash
mvn clean install
```

2. **Run the Spring Boot application:**

```bash
mvn spring-boot:run
```

Alternatively, you can run the generated JAR file:

```bash
java -jar target/websocket-chat-0.0.1-SNAPSHOT.jar
```

3. **Access the application:**

Open your browser and navigate to:
```
http://localhost:8080/index.html
```

## 💬 How to Use

1. **Open the application** in two different browser windows or tabs:
   ```
   http://localhost:8080/index.html
   ```

2. **Log in with different usernames** in each window.

3. **Select a user** from the "Connected Users" list on the left.

4. **Start chatting!** Type your message in the input field and click "Send" or press Enter.

5. **Watch for notifications** when users join or leave the chat.




