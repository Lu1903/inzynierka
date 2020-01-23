const $_documentContainer = document.createElement('template');
$_documentContainer.innerHTML = `
<custom-style>
    <style>
        html{
        --fc-day-grid-event_fc-time-color: #ff0000 !important;
       }
    </style>
</custom-style>
`;
document.head.appendChild($_documentContainer.content);