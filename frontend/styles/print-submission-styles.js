const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<custom-style> 
  <style>
    #print-submission {
      font-size: 16px;
    }
    #print-submission .section {
      min-width: 880px;
      border-style: solid;
      margin: 16px 0;
      padding: 16px;
    }
    #print-submission .section &gt; div {
      margin: 5px 0;
    }
    #print-submission .two-columns {
      height: auto;
      overflow: hidden;
    }
    #print-submission .two-columns span {
      display: inline-block;
    }
    #print-submission .two-columns .left {
      min-width: 400px;
      width: auto;
      overflow: hidden;
    }
    #print-submission .two-columns .right {
      width: 400px;
      margin-left: 32px;
      float: right;
    }
    #print-submission .user-information .label {
      min-width: 80px;
    }
    #print-submission .sample-information .label {
      min-width: 160px;
    }
    #print-submission .sample-information span {
      display: inline-block;
    }
    #print-submission .sample-name-separator {
      margin-right: 5px;
    }
    #print-submission .service-information .label {
      vertical-align: top;
      min-width: 240px;
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
    #print-submission .files table {
      border-collapse: collapse;
    }
    #print-submission .files th, #print-submission .files td {
      border-style: solid;
      border-width: 1px;
      border-color: black;
    }
    #print-submission .files td {
      margin: 0;
      padding: 0;
    }
    #print-submission .plate-information {
      width: 1248px;
    }
    #print-submission .plate-information table {
      border-collapse: collapse;
    }
    #print-submission .plate-information th, #print-submission .plate-information td {
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
      width: 1056px;
    }
    #print-submission .plate-information {
      transform: rotate(90deg) translateX(202px);
    }
    #print-submission .pagebreak {
      page-break-before: always;
    }
  </style> 
 </custom-style>`;

document.head.appendChild($_documentContainer.content);

