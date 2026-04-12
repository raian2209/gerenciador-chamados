(function () {
    function initSidebarToggle() {
        var sidebar = document.querySelector("[data-sidebar]");
        var toggle = document.querySelector("[data-sidebar-toggle]");
        if (!sidebar || !toggle) {
            return;
        }

        toggle.addEventListener("click", function () {
            sidebar.classList.toggle("is-open");
        });

        document.addEventListener("click", function (event) {
            if (window.innerWidth > 980) {
                return;
            }

            if (!sidebar.contains(event.target) && !toggle.contains(event.target)) {
                sidebar.classList.remove("is-open");
            }
        });
    }

    window.AppDom.onReady(initSidebarToggle);
})();
