gantt = null;
threads = null;
devises = null;
traceCount = 0;
traceMap = null;
ready = null;
blocks = null;
$(document).ready(function() {
    gantt = JSON.parse(getParameterByName("gantt"));
    threads = JSON.parse(getParameterByName("threads"));
    devises = parseInt(getParameterByName("devises"));
    ready = JSON.parse(getParameterByName("ready"));
    blocks = new Array(3);
    blocks[0] = (JSON.parse(getParameterByName("block"))).b0;
    blocks[1] = (JSON.parse(getParameterByName("block"))).b1;
    blocks[2] = (JSON.parse(getParameterByName("block"))).b2;
    console.log(blocks[0]);
    traceMap = new Map();
    (function(count){    
        $.each(threads, function(i,value) {
            traceCount += value.ULT;
            for(var j=1; j<=value.ULT; j++){
                traceMap.set((i+1)*100 + j, count);
                count += 1;
            }
        });
    })(0);
    traceCount += devises;
    traceCount += 1;
    var boxHeight = (40 * (traceCount+1) + 20);
    $("#tracerBox").css("height", boxHeight + "px");
    $("#quantums").css("margin-top", (boxHeight - 40) + "px");
    for (var i = 0; i<traceCount; i++){
        $("#traceContainer").append('<div class="trace" id="trace'+ i +'"></div>');
        $("#trace"+ i).css("margin-top","30px");
    }
    for (var key of traceMap.keys()) {
        var label = "KLT" + parseInt(key/100) + " ULT" + key%100 + "-";
        $("#tLabels").append('<div class="tLab">'+ label +'</div>');
    }
    for (var i = 1; i <= devises; i++) {
        var label = "Device " + i + "-";
        $("#tLabels").append('<div class="tLab">'+ label +'</div>');
    }
    var label = "Idle process (SO)-";
    $("#tLabels").append('<div class="tLab">'+ label +'</div>');
    $(".tLab").css("margin-top","23px");
    $("#quantums").append('<span class="qLabels" style="color: black;width:36px">0</span>');
    drawGantt();
});

function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function drawGantt() {
    var actualTime = parseInt(gantt[0].Time);
    var ran = [];
    var ganttSize = Object.keys(gantt).length;
    for(var index = 0; index < ganttSize; index++){
        if (actualTime < parseInt(gantt[index].Time)) {
            actualTime = parseInt(gantt[index].Time);
            nextLine(ran,actualTime);
            ran = [];
        }
        if(gantt[index].Run == "CPU"){
            i = traceMap.get(parseInt(gantt[index].KLT) * 100 + parseInt(gantt[index].ULT));
            core = parseInt(gantt[index].Core);
            var color = "blue";
            if (core == 1) {
                color = "red";
            }
            $("#trace"+ i).append('<div class="run" style="background-color: '+ color +';"></div>');
            ran.push(i);
        }
        if(gantt[index].Run == "SO"){
            i = traceCount - 1;
            if(!ran.includes(i)){    
                core = parseInt(gantt[index].Core);
                var color = "blue";
                if (core == 1) {
                    color = "red";
                }
                $("#trace"+ i).append('<div class="run" id="t'+((actualTime*100)+i)+'" style="background: '+ color +';"></div>');
                ran.push(i);
            } else {
                $("#t"+((actualTime*100)+i)).css("background","linear-gradient(to bottom right, red, blue)");
            }
        }
        if(gantt[index].Run == "IO"){
            i = traceCount - devises - 1 + parseInt(gantt[index].Device);
            var color = "green";//"linear-gradient(to bottom right, red, blue)";
            var text = gantt[index].KLT;
            var title = "KLT" + gantt[index].KLT + " ULT" + gantt[index].ULT;
            $("#trace"+ i).append('<div class="run" title="'+title+'" style="background: '+ color +';">KLT'+ text +'</div>');
            ran.push(i);
        }
    }
    $( document ).tooltip();
}

function nextLine(ran,t) {
    for (var i = 0; i < traceCount; i++) {
        if(!ran.includes(i)){
            $("#trace"+ i).append('<div class="run"></div>');
        }
    }
    var readyText = "Ready queue:";
    if((t-1) < Object.keys(ready).length){
        var arr = ready[t-1].split(" ");
        for (var i=0; i< arr.length-1; i++){
            readyText += " KLT" + arr[i];
        }
    }
    readyText += "\nDevise 1:"
    if((t-1) < Object.keys(blocks[0]).length){
        var arr = blocks[0][t-1].split(" ");
        for (var i=0; i< arr.length-1; i++){
            readyText += " KLT" + arr[i];
        }
    }
    readyText += "\nDevise 2:"
    if((t-1) < Object.keys(blocks[1]).length){
        var arr = blocks[1][t-1].split(" ");
        for (var i=0; i< arr.length-1; i++){
            readyText += " KLT" + arr[i];
        }
    }
    readyText += "\nDevise 3:"
    if((t-1) < Object.keys(blocks[2]).length){
        var arr = blocks[2][t-1].split(" ");
        for (var i=0; i< arr.length-1; i++){
            readyText += " KLT" + arr[i];
        }
    }
    $("#quantums").append('<span class="qLabels" title="'+ readyText +'">'+ t +'</span>');
}