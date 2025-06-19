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