requireAuth();
renderNavbar("mentorship");

let currentTab = "incoming";

function switchTab(tab) {
    currentTab = tab;
    document.getElementById("tabIncoming").classList.toggle("active", tab === "incoming");
    document.getElementById("tabOutgoing").classList.toggle("active", tab === "outgoing");
    loadRequests();
}

async function loadRequests() {
    try {
        const requests = await apiFetch("/matches/" + currentTab);
        renderRequests(requests);
    } catch (err) {
        alert(err.message);
    }
}

function renderRequests(requests) {
    const container = document.getElementById("requestsList");

    if (requests.length === 0) {
        container.innerHTML = `<div class="empty-state">No ${currentTab} requests yet.</div>`;
        return;
    }

    container.innerHTML = requests.map(r => {
        const otherPerson = currentTab === "incoming" ? r.menteeName : r.mentorName;

        let actions = "";
        if (currentTab === "incoming" && r.status === "PENDING") {
            actions = `
                <div style="display:flex; gap:10px; margin-top:12px;">
                    <button class="btn btn-success" onclick="respond(${r.id}, 'accept')">Accept</button>
                    <button class="btn btn-secondary" onclick="respond(${r.id}, 'reject')">Decline</button>
                </div>
            `;
        } else if (r.status === "ACCEPTED") {
            actions = `<button class="btn btn-secondary" style="margin-top:12px;" onclick="endMatch(${r.id})">End Mentorship</button>`;
        }

        const phone = r.status === "ACCEPTED" && r.contactPhoneNumber
            ? `<div class="phone-reveal">📞 Contact: ${r.contactPhoneNumber}</div>`
            : "";

        return `
            <div class="card mentor-card">
                <div class="mentor-card-header">
                    <strong>${otherPerson}</strong>
                    <span class="status-pill status-${r.status.toLowerCase()}">${r.status}</span>
                </div>
                <p class="item-meta">Interest: ${r.interest} · Score: ${r.matchScore || "N/A"}</p>
                ${r.message ? `<p class="item-desc">"${r.message}"</p>` : ""}
                ${phone}
                ${actions}
            </div>
        `;
    }).join("");
}

async function respond(id, action) {
    try {
        await apiFetch(`/matches/${id}/${action}`, { method: "POST" });
        loadRequests();
    } catch (err) {
        alert(err.message);
    }
}

async function endMatch(id) {
    if (!confirm("End this mentorship? This can't be undone.")) return;
    try {
        await apiFetch(`/matches/${id}/end`, { method: "POST" });
        loadRequests();
    } catch (err) {
        alert(err.message);
    }
}

loadRequests();