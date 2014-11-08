======================
banner css .banner

banner-1 css .banner .title
banner-2 css .banner .desc
banner-3 css .banner .price
======================


@@ if
---------------
banner
    visible
@@ do
---------------
[1 - ${count("banner-*")}]
banner-@
    visible
---------------
@@ end