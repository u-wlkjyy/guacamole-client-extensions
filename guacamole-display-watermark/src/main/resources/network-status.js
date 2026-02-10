(function () {

    function init() {
        if (document.getElementById('network-status-widget')) return;

        // 1. Create the Widget UI
        var widget = document.createElement('div');
        widget.id = 'network-status-widget';
        widget.className = 'status-check';

        var dot = document.createElement('div');
        dot.id = 'network-status-dot';

        var text = document.createElement('span');
        text.id = 'network-status-text';
        text.innerText = 'Checking...';

        widget.appendChild(dot);
        widget.appendChild(text);
        document.body.appendChild(widget);

        // 2. Session Detection Logic
        function checkSessionState() {
            var hash = window.location.hash;
            // Guacamole client URL starts with #/client/
            // Example: #/client/c/GUID or #/client/base64
            var isClient = hash && hash.indexOf('#/client/') === 0;

            if (isClient) {
                document.body.classList.add('guac-session-active');
                return true;
            } else {
                document.body.classList.remove('guac-session-active');
                return false;
            }
        }

        // 3. Monitoring Logic
        function checkLatency() {
            // Only ping if we are in a session
            // This prevents unnecessary network traffic on login page
            if (!checkSessionState()) {
                return;
            }

            var start = Date.now();
            fetch('', { method: 'HEAD', cache: 'no-store' })
                .then(function () {
                    var end = Date.now();
                    var rtt = end - start;
                    updateDisplay(rtt);
                })
                .catch(function (err) {
                    // console.warn("Network check failed:", err);
                    updateDisplay(999);
                });
        }

        function updateDisplay(rtt) {
            text.innerText = rtt + ' ms';

            var statusClass = 'status-good';
            if (rtt >= 999) {
                statusClass = 'status-poor';
                text.innerText = 'Offline';
            } else if (rtt > 150) {
                statusClass = 'status-poor';
            } else if (rtt > 50) {
                statusClass = 'status-fair';
            }

            widget.className = statusClass;
        }

        // Check network latency every 2 seconds
        setInterval(checkLatency, 2000);

        // Check session state frequently to respond to fast navigation
        setInterval(checkSessionState, 500);

        // Initial check
        checkSessionState();
    }

    // Wait for DOM to be ready
    if (document.readyState === 'loading') {
        window.addEventListener('load', init);
    } else {
        init();
    }

})();
