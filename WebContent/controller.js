class Controller {

    constructor() {
        var self = this;
        $().ready(function () {
            self.onLoad();
        });
    }
    
    onLoad() {
        console.log("Loaded.");

        var items = [];

        // attach listener to button
        $('#addButton').click(function() {
            addItem();
        });

        $('#sendButton').click(function() {
            sendItems();
        });


        // Add the animation item to the list
        var addItem = function () {
            var color = $('#color').val();
            var displayTime = $('#displayTime').val();
            var transitionTime = $('#transitionTime').val();
            var transitionType = $('input[name=transitionType]:checked').val();

            var animationItem = {};
            animationItem.color = color;
            animationItem.displayTime = displayTime;
            animationItem.transitionTime = transitionTime;
            animationItem.transitionType = transitionType;

            items.push(animationItem);

            renderItems();
        };

        var sendItems = function() {
            $.post("./LightWebServlet", JSON.stringify(items), "json");
        };


        var renderItems = function() {
            $('#animationItems').empty();

            var itemsHTML = "";

            items.forEach(function(element) {
                itemsHTML += ("<div><p>Color: " + element.color +
                "</p><p>Display Time: " + element.displayTime +
                "</p><p>Transition Time: " + element.transitionTime +
                "</p><p>Transition Type: " + element.transitionType + "</p></div>");
            });

            $('#animationItems').html(itemsHTML);
        };

    }

}