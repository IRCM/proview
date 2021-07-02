const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<custom-style> 
  <style>
    #print-submission .section {
      border-style: solid;
      margin: 1em 0;
      padding: 1em;
    }
    #print-submission .section &gt; div {
      margin: 0.3em 0;
    }
    #print-submission .two-columns {
      height: auto;
      overflow: hidden;
    }
    #print-submission .two-columns span {
      display: inline-block;
    }
    #print-submission .two-columns .left {
      width: auto;
      overflow: hidden;
    }
    #print-submission .two-columns .right {
      width: 25em;
      margin-left: 2em;
      float: right;
    }
    #print-submission .user-information .label {
      min-width: 5em;
    }
    #print-submission .sample-information .label {
      min-width: 10em;
    }
    #print-submission .sample-information span {
      display: inline-block;
    }
    #print-submission .sample-name-separator {
      margin-right: 0.3em;
    }
    #print-submission .service-information .label {
      vertical-align: top;
      min-width: 15em;
    }
    #print-submission .service-information span {
      display: inline-block;
    }
    #print-submission .solvent-separator {
      margin-right: 0.3em;
    }
    #print-submission .comment .label {
      vertical-align: top;
      min-width: 5em;
    }
    #print-submission .comment span {
      display: inline-block;
      margin: 0;
      padding: 0;
    }
    #print-submission .plate-information table {
      border-collapse: collapse;
    }
    #print-submission .plate-information th, .plate-information td {
      border-style: solid;
      border-width: 0 1px 1px 0;
      border-color: black;
    }
    #print-submission .plate-information tbody tr th:first-child {
      border-width: 0 1px 1px 1px;
    }
    #print-submission .plate-information tbody tr:first-child th {
      border-width: 1px 1px 1px 0;
    }
    #print-submission .plate-information tbody tr:first-child th:first-child {
      border-width: 1px;
    }
    #print-submission .plate-information td {
      width: 102px;
      height: 102px;
      margin: 0;
      padding: 0;
    }
    #print-submission .plate-information td.banned {
      color: white;
      background-color: red;
    }
    #print-submission .plate-information span {
      margin: 1px;
      display: inline-block;
      width: 96px;
      height: 96px;
      word-wrap: break-word;
      overflow: hidden;
    }
  </style> 
  <style media="print">
    #print-submission .section {
      width: 11in;
    }
    #print-submission .plate-information {
      width: 13in;
      transform: rotate(90deg) translateX(2.1in); 
    }
    #print-submission .plate-information td {
      width: 1.05in;
      height: 1.05in;
    }
    #print-submission .plate-information span {
      width: 0.8in;
      height: 0.8in;
    }
    #print-submission .pagebreak {
      page-break-before: always;
    }
  </style> 
 </custom-style>`;

document.head.appendChild($_documentContainer.content);

