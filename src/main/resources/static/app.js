'use strict';

let stompClient = null;
let username = null;
let selectedUser = null;

const connect = (event) => {
    username = document.getElementById('username').value.trim();

    if (username) {
        document.getElementById('login-page').style.display = 'none';
        document.getElementById('chat-page').style.display = 'block';

        const socket = new SockJS('/ws-chat');
        stompClient = Stomp.over(socket);

        stompClient.debug = function(str) {
            console.log('STOMP: ' + str);
        };

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
};

const onConnected = () => {
    console.log('Connected to WebSocket server as: ' + username);

    stompClient.subscribe('/topic/public', onPublicMessageReceived);

    stompClient.subscribe('/topic/users', onUserListReceived);

    stompClient.subscribe(`/user/${username}/queue/messages`, onPrivateMessageReceived);

    stompClient.send('/app/chat.register',
        {},
        JSON.stringify({
            sender: username,
            type: 'JOIN'
        })
    );

    stompClient.send('/app/chat.userlist', {}, JSON.stringify({}));
};

const onError = (error) => {
    console.error('WebSocket connection error', error);
    alert('Cannot connect to server. Please try again later.');
};

const onPublicMessageReceived = (payload) => {
    const message = JSON.parse(payload.body);
    console.log('Public message received:', message);

    if (message.type === 'JOIN' || message.type === 'LEAVE') {
        const messageElement = document.createElement('div');
        messageElement.classList.add('event-message');
        messageElement.textContent = message.content;

        document.getElementById('chat-messages').appendChild(messageElement);

        stompClient.send('/app/chat.userlist', {}, JSON.stringify({}));
    }
};

const onUserListReceived = (payload) => {
    const users = JSON.parse(payload.body);
    console.log('User list received:', users);

    const userListElement = document.getElementById('usersList');

    userListElement.innerHTML = '';

    Object.keys(users).forEach(key => {
        if (key !== username) {
            const userElement = document.createElement('li');
            userElement.classList.add('list-group-item', 'user-item');
            userElement.textContent = users[key];
            userElement.setAttribute('data-username', key);

            if (key === selectedUser) {
                userElement.classList.add('active');
            }

            userElement.addEventListener('click', selectUser);
            userListElement.appendChild(userElement);
        }
    });

    if (userListElement.children.length === 0) {
        const noUsersElement = document.createElement('li');
        noUsersElement.classList.add('list-group-item');
        noUsersElement.textContent = 'No other users online';
        userListElement.appendChild(noUsersElement);
    }
};

const onPrivateMessageReceived = (payload) => {
    const message = JSON.parse(payload.body);
    console.log('Private message received:', message);
    displayMessage(message);
};

const selectUser = (event) => {
    document.querySelectorAll('.user-item.active').forEach(item => {
        item.classList.remove('active');
    });

    event.target.classList.add('active');

    selectedUser = event.target.getAttribute('data-username');
    document.getElementById('selectedUser').textContent = event.target.textContent;

    document.getElementById('message').disabled = false;
    document.getElementById('sendButton').disabled = false;
};

const sendMessage = (event) => {
    const messageContent = document.getElementById('message').value.trim();

    if (messageContent && stompClient && selectedUser) {
        const chatMessage = {
            sender: username,
            recipient: selectedUser,
            content: messageContent,
            type: 'CHAT'
        };

        console.log('Sending message:', chatMessage);
        stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(chatMessage));

        displayMessage(chatMessage);

        document.getElementById('message').value = '';
    }
    event.preventDefault();
};

const displayMessage = (message) => {
    const chatContainer = document.getElementById('chat-messages');
    const messageElement = document.createElement('div');

    const isMessageFromMe = message.sender === username;

    messageElement.classList.add('chat-message');
    if (isMessageFromMe) {
        messageElement.classList.add('chat-message-sent');
    } else {
        messageElement.classList.add('chat-message-received');
    }

    const infoElement = document.createElement('div');
    infoElement.classList.add('message-info');
    infoElement.textContent = isMessageFromMe ? 'You' : message.sender;

    const contentElement = document.createElement('div');
    contentElement.textContent = message.content;

    messageElement.appendChild(infoElement);
    messageElement.appendChild(contentElement);

    chatContainer.appendChild(messageElement);

    chatContainer.scrollTop = chatContainer.scrollHeight;
};

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('loginForm').addEventListener('submit', connect);
    document.getElementById('messageForm').addEventListener('submit', sendMessage);

    document.getElementById('username').focus();
});

window.addEventListener('beforeunload', () => {
    if (stompClient && username) {
        stompClient.send('/app/chat.leave',
            {},
            JSON.stringify({
                sender: username,
                type: 'LEAVE'
            })
        );

        stompClient.disconnect();
    }
});