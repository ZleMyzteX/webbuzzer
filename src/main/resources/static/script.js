const STORAGE_KEY = "clientId";
const input = document.getElementById("clientIdInput");
const status = document.getElementById("status");
const saveButton = document.getElementById("idSaveButton");
const sendButton = document.getElementById("buzzer");
const reconnectButton = document.getElementById("reconnectButton");

let ws = null;
let serverTimeOffset = 0;

// Synchronize with server time using /api/server-time
async function syncServerTime() {
  console.log("Synchronizing server time");
  const t0 = performance.now();
  const response = await fetch("/api/server-time");
  const serverTime = await response.json(); // { now: <ms since epoch> }
  const t1 = performance.now();
  const rtt = (t1 - t0) / 2;
  serverTimeOffset = serverTime.now - (performance.timeOrigin + t0 + rtt);
  console.log('offset: ', serverTimeOffset);
  updateStatus("Server time synchronized.");
}

function generateClientId() {
  if (crypto.randomUUID) {
    return crypto.randomUUID();
  }
  // No HTTPS -> no crypto :( fallback to custom blabla
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

function loadClientId() {
  let clientId = localStorage.getItem(STORAGE_KEY);
  if (!clientId) {
    clientId = generateClientId();
    localStorage.setItem(STORAGE_KEY, clientId);
  }
  return clientId;
}

function saveClientId() {
  const newId = input.value.trim();
  if (!newId) {
    alert("Client ID cannot be empty.");
    return false;
  }
  localStorage.setItem(STORAGE_KEY, newId);
  updateStatus(`ID saved: ${newId}`);
  return true;
}

function updateStatus(message) {
  status.textContent = message;
}

function setupWebSocket() {
  if (ws && ws.readyState === WebSocket.OPEN) {
    updateStatus("Already connected.");
    return;
  }

  ws = new WebSocket(`ws://${location.host}/timestamps`);

  ws.onopen = () => updateStatus("WebSocket connected.");
  ws.onerror = () => updateStatus("WebSocket error. :(");
  ws.onclose = () => updateStatus("WebSocket disconnected.");
}

function sendTimestamp() {
  const clientId = input.value.trim();
  if (!clientId) {
    updateStatus("Client ID cannot be empty.");
    return;
  }

  const timestampNs = getAccurateTimestampNs();
  const message = JSON.stringify(
      {clientId, timestampNs: timestampNs.toString()});

  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(message);
    updateStatus(`Sent timestamp for ID ${clientId}`);
  } else {
    updateStatus("Cannot send - WebSocket not connected.");
  }
}

// Get accurate timestamp in nanoseconds, synchronized to server
function getAccurateTimestampNs() {
  return BigInt(
      Math.floor(performance.timeOrigin + performance.now() + serverTimeOffset)
      * 1_000_000);
}

async function fetchLobbies() {
  const response = await fetch('/api/lobbies');
  if (!response.ok) return [];
  return response.json();
}

function renderLobbies(lobbies) {
  const list = document.createElement('ul');
  lobbies.forEach(lobby => {
    const item = document.createElement('li');
    item.textContent = lobby.displayName; // adjust property as needed
    item.style.cursor = 'pointer';
    item.onclick = () => registerForLobby(lobby.id);
    list.appendChild(item);
  });
  const container = document.getElementById('lobbyList');
  container.innerHTML = '';
  container.appendChild(list);
}

async function registerForLobby(lobbyId) {
  const clientId = input.value.trim();
  if (!clientId) {
    alert("Client ID required.");
    return;
  }
  const formData = new URLSearchParams({ clientId });
  const response = await fetch(`/api/lobby/${lobbyId}/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: formData
  });
  if (response.ok) {
    alert('Registered!');
  } else {
    alert('Registration failed');
  }
}

// On page load
document.addEventListener('DOMContentLoaded', async () => {
  const lobbies = await fetchLobbies();
  renderLobbies(lobbies);
});

document.getElementById("createLobbyButton").addEventListener("click", async () => {
  const displayName = prompt("Enter lobby name:");
  if (!displayName) return;
  const clientId = input.value.trim();
  if (!clientId) {
    alert("Client ID required.");
    return;
  }
  const formData = new URLSearchParams({ displayName, clientId });
  const response = await fetch("/api/lobby", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: formData
  });
  if (response.ok) {
    alert("Lobby created!");
    const lobbies = await fetchLobbies();
    renderLobbies(lobbies);
  } else {
    alert("Failed to create lobby");
  }
});

window.addEventListener("DOMContentLoaded", syncServerTime);
saveButton.addEventListener("click", saveClientId);
sendButton.addEventListener("click", sendTimestamp);
reconnectButton.addEventListener("click", () => {
  if (ws) {
    ws.close();
  }
  updateStatus("Reconnecting...");
  setupWebSocket();
  syncServerTime();
});

input.value = loadClientId();
setupWebSocket();