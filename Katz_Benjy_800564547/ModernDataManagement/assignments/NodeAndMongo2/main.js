const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const app = express();
app.use(bodyParser.urlencoded({ extended: true }));



app.get('/', function(req, res){
  res.send(`
  <title>NodeAndMongo2Web-Application</title>
  <h2>NodeAndMongo2Web-Application</h2>
  <h2><b> <a href="/contact">add subscriber</a></b></h2>
  `);
});


app.get('/contact', function(req, res) {

  res.send(`<title>Contact</title>
  <h1>Subscribe!</h1>
  <h3>Enter your email if you are interested to learn more</h3>
  
  <form action='/contact' method="POST" autocomplete="off">
      <div class = "form_group">
      <label>Name</label>
      <input type="text" class="form-control" name="name" required>
      </div>
      <div class = "form_group">
      <label>Email</label>
      <input type="text" class="form-control" name="email" required>
      </div>
      <div class = "form_group">
      <label>Zip Code</label>
      <input type="number" class="form-control" name="zipCode" required>
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

mongoose.connect('mongodb://localhost/NodeAndMongo2_db', { useNewUrlParser: true });


const subSchema = new mongoose.Schema({
  name: String,
  email: String,
  zipCode: Number
});


const Subscriber = mongoose.model('Subscriber', subSchema);

app.post('/contact', async function(req, res) {
    newSub = new Subscriber({
      name: req.body.name,
      email: req.body.email,
      zipCode: req.body.zipCode
    });
    await newSub.save();
    res.redirect('/subscribers');
  });

  app.get('/subscribers', async (req, res) => {

    const users = await Subscriber.find({});
    let data = `<style>
    table, th, td {
      border:1px solid black;
    }
    </style> <table> <tr>
    <th>Name</th>
    <th>Email</th>
    <th>Zipcode</th>
  </tr>`;
    for (let i = 0; i < users.length; i++){
      if(users[i].name !=null||users[i].email !=null||users[i].zipCode !=null){
      data = data+
    `<tr>
    <td>`+ users[i].name+`</td>`+
    `<td>`+users[i].email+`</td>`+
    `<td>`+users[i].zipCode+`</td>
    </tr>`};

    }
    res.send(data+`</table>`);
  });
  app.all('*', function(req, res){
    res.status(404).send('<h3>404: Not Found</h3>');
  });
  