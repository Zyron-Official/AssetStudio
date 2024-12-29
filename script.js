document.addEventListener('contextmenu', event => event.preventDefault());
document.addEventListener("DOMContentLoaded", function () {
    const header = document.querySelector(".header");
    const footer = document.querySelector(".footer");
    const navigationDrawer = document.querySelector(".navigation-drawer");
    const toolbar = document.querySelector("mdui-top-app-bar[id='toolbar']");
    const menuButton = document.querySelector("mdui-button-icon[id='mdui-menu']");
    const closeButton = document.querySelector("mdui-button[id='close']");

    if (toolbar) {
        window.addEventListener("scroll", () => {
            if (document.documentElement.scrollTop > 0) {
                toolbar.style.backgroundColor = "rgb(var(--mdui-color-surface-container))"; 
            }
            else {
                toolbar.style.backgroundColor = "";
            }
        });
    }
    else {
        console.error("Toolbar element not found!");
    }

    if (menuButton && navigationDrawer) {
        menuButton.addEventListener("click", () => {
            navigationDrawer.open = true;
        });
    }
    else {
        console.error("Menu button or navigation drawer not found!");
    }

    if (closeButton && navigationDrawer) {
        closeButton.addEventListener("click", () => {
            navigationDrawer.open = false;
        });
    }
    else {
        console.error("Close button or navigation drawer not found!");
    }
});

document.addEventListener('DOMContentLoaded', function () {
    const hamburgerIcon = document.getElementById('mdui-menu');
    const nav = document.getElementById('nav');
    const drawer = document.getElementById('drawer');

    const toggleDrawer = function () {
        if (drawer.classList.contains('closed')) {
            drawer.style.left = '0';
            drawer.classList.remove('closed');
            nav.classList.add('show-mobile');
        }
        else {
            drawer.style.left = '-300px';
            drawer.classList.add('closed');
            nav.classList.remove('show-mobile');
        }
    };

    hamburgerIcon.addEventListener('click', toggleDrawer);

    document.addEventListener('click', function (event) {
        const isHamburgerIcon = event.target === hamburgerIcon || hamburgerIcon.contains(event.target);
        const isDrawer = event.target === drawer || drawer.contains(event.target);

        if (!isHamburgerIcon && !isDrawer) {
            drawer.style.left = '-300px';
            drawer.classList.add('closed');
            nav.classList.remove('show-mobile');
        }
    });
});