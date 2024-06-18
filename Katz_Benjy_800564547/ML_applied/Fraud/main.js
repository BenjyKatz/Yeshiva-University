const express = require('express');
//const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const app = express();
app.use(bodyParser.urlencoded({ extended: true }));


const fetch = require('node-fetch'); // CommonJS require
const Request = require('node-fetch');


app.get('/', function(req, res){
  res.send(`
  <title>NodeAndMongo2Web-Application</title>
  <h2>NodeAndMongo2Web-Application</h2>
  <h2><b> <a href="/transaction">fraud check</a></b></h2>
  `);
});
// top_columns = ["V257","V246", "V244","V242","V201","V200","V189","V188","V258",
// "V45","V158","V156","V149","V228","V44","V86","V87","V170","V147","V52"]

// _KEEP_COLUMNS_MODEL_TxnPcd = ['TransactionAmt', 'ProductCD']

// _KEEP_COLUMNS_MODEL_1_5 = ['ProductCD', 'TransactionAmt', 'card1', 'card2', 'card3', 'card4',
//                       'card5', 'card6', 'P_emaildomain']
// norm_data = ['ProductCD', 'card4','card6', 'P_emaildomain']

app.get('/transaction', function(req, res) {


  res.send(`<title>Transaction info</title>
 
  
  <form action='/transaction' method="POST" autocomplete="off">
      <div class = "form_group">
      <label>TransactionAmt</label>
      <input type="number" class="form-control" name="TransactionAmt" required>
      </div>

      <div class = "form_group">
      <label>ProductCD</label>
      <input type="text" class="form-control" name="ProductCD" required>
      </div>

      <div class = "form_group">
      <label>card1</label>
      <input type="number" class="form-control" name="card1">
      </div>

      <div class = "form_group">
      <label>card2</label>
      <input type="number" class="form-control" name="card2">
      </div>

      <div class = "form_group">
      <label>card3</label>
      <input type="number" class="form-control" name="card3">
      </div>

      <div class = "form_group">
      <label>card4</label>
      <input type="text" class="form-control" name="card4">
      </div>

      <div class = "form_group">
      <label>card5</label>
      <input type="number" class="form-control" name="card5">
      </div>

      <div class = "form_group">
      <label>card6</label>
      <input type="text" class="form-control" name="card6">
      </div>

      <div class = "form_group">
      <label>P_emaildomain</label>
      <input type="text" class="form-control" name="P_emaildomain">
      </div>

      <div class = "form_group">
      <label>email</label>
      <input type="text" class="form-control" name="email">
      </div>

      <div class="form-group">
      <button type="submit" class="btn btn-info">Submit</button>
      </div>

      
  </form>`);
});


const port = 3000;
app.listen(port, function() {
  console.log(`Listening on port ${port}...`);
});

//mongoose.connect('mongodb://localhost/NodeAndMongo2_db', { useNewUrlParser: true });

/*
const subSchema = new mongoose.Schema({
  name: String,
  email: String,
  zipCode: Number
});*/


//const Subscriber = mongoose.model('Subscriber', subSchema);
//async function
app.post('/transaction', async function(req, res) {
    const transObject = {
      TransactionAmt: req.body.TransactionAmt,
      ProductCD: req.body.ProductCD,
      card1: req.body.card1,
      card2: req.body.card2,
      card3: req.body.card3,
      card4: req.body.card4,
      card5: req.body.card5,
      card6: req.body.card6,
      P_emaildomain: req.body.P_emaildomain,
      email: req.body.email
    };

    
    const request = new Request('http://localhost:5000/predict', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json', // Specify the content type as JSON
      },
      body: JSON.stringify(transObject), // Convert the data to a JSON string
    });
    // Make an HTTP GET request using the fetch API
    try {
      // Make the POST request using the fetch function
      const response = await fetch('https://googlefraudserver-flks2qc3qq-ue.a.run.app/predict', {
        method: 'POST', // or 'GET' or any other HTTP method
        headers: {
          'Content-Type': 'application/json',
          // Add any other headers as needed
        },
        body: JSON.stringify(transObject),
      });
  
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
  
      // Parse the JSON response
      const responseData = await response.json();
      
      console.log(responseData); // Process the response data
      res.send(
        `
      <header>
      Is fraud: ` +responseData.result + 
     ` <\header>`
      );
    } catch (error) {
      console.error('Fetch error:', error); // Handle any errors that occurred during the fetch
    }


    //await newSub.save();
    //res.redirect('/subscribers');
  });


  app.all('*', function(req, res){
    res.status(404).send('<h3>404: Not Found</h3>');
  });
  