

grouped(["mobile"], function () {

    test("Test A", function (){
    });

    grouped(["tablet", "desktop"], function () {
        test("Test B", function (){
        });

        test("Test C", function (){
        });
    });

});


test("Test D", function () {
});