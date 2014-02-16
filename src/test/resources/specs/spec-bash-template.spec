
======================
obj-1    id obj1
obj-2    id obj2
obj-3    id obj3
obj-4    id obj4
======================


# This expression should provide a range [1 - 3]
[ 1 - ${count("obj-*") - 1}]
obj-@
    height: 10px