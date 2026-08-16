function renderNavbar(active) {
    const el = document.getElementById("navbar");
    if (!el) return;

    const links = [
        { key: "dashboard", label: "Dashboard", href: "dashboard.html" },
        { key: "lostfound", label: "Lost & Found", href: "items.html" },
        { key: "mentorship", label: "Mentorship", href: "mentorship.html" },
        { key: "businesses", label: "Businesses", href: "businesses.html" },
        { key: "marketplace", label: "Marketplace", href: "listings.html" }
    ];

    el.innerHTML = `
        <div class="nav-inner">
            <a href="dashboard.html" class="nav-logo">CampusVerse</a>
            <div class="nav-links">
                ${links.map(l => `<a href="${l.href}" class="nav-link ${active === l.key ? "active" : ""}">${l.label}</a>`).join("")}
            </div>
            <div class="nav-right">
                <span class="nav-user">${getUserName() || ""}</span>
                <button class="btn-ghost" onclick="logout()">Sign Out</button>
            </div>
        </div>
    `;
}