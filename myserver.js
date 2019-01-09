/* myserver.js
 * - Server for application.
 */

// Assign http module to http variable for server.
var http = require('http');
var url = require('url');
var querystring = require('querystring');

// Use Express.
const express = require('express')
const app = express();
//app.use(express.json({limit : '50mb'}));
//app.use(express.urlencoded({extended : true}));
//app.use(express.urlencoded({limit : '50mb'}));
var bodyParser = require('body-parser');
app.use(bodyParser.json({limit: '50mb'}));
app.use(bodyParser.urlencoded({limit: '50mb', extended: true}));

// For Tab2 - File read and write.
var fs_read = require('fs');


// Connect this nodejs server with mongoDB.
var mongoose = require('mongoose');
mongoose.connect('mongodb://localhost:27017/mydb');
var db = mongoose.connection;

db.on('error', function(){
	console.log('Connection Failed.');
});

db.once('open', function(){
	console.log('Connected.');
});

var Schema = mongoose.Schema;

// Server listens for port with port number 8080.
// Print log to certain that the server is running.
app.listen(80, function(){ 
	console.log('Server is running...');
});

// For Tab1 - Get contact_address.
// Require only one argument which is user identifier.
app.get('/getcontact',function(req, res){
	// Parse arguments.
	var parsedUrl = url.parse(req.url);
	var parsedQuery = querystring.parse(parsedUrl.query, '&', '=');
	
	// Need just one argument which is user identifier.
	if (Object.keys(parsedQuery).length != 1){
		res.write('Error: Too many or no argument.');
		res.end();
	}else{
		var user_id = parsedQuery.user;
		var Contact = mongoose.model('Schema', new Schema({user : String, contact : [{name : String, number : String}]}), 'contact_address');
		
		Contact.find({"user" : user_id}, {"_id" : 0, "contact" : 1}, function(error, data){
			console.log('Get contact address.');
			if (error){
				console.log(error);
			}else{
				// If there's no entity for this user, make it.
				if (data.toString() == ""){
					Contact.create({"user" : user_id, "contact" : []}, function(error){
						console.log('new');
						// Set the respond again.
						Contact.find({"user" : user_id}, {"_id" : 0, "contact" : 1}, function(error, newdata){
							if (error){
								console.log(error);
							}else{
								data = newdata;
								res.setHeader("Content-Type", "text/json");
								res.write(data.toString());
								res.end();
							}
						});
					});
				}else{
					res.setHeader("Content-Type", "text/json");
					res.write(data.toString());
					res.end();
				}
			}
		});
		mongoose.deleteModel('Schema');
	}
});

// For Tab1 - Modify contact_address.
// Require only one argumnet which is user identifier.
app.post('/postcontact', function(req, res){
	console.log('Post contact address.');
	// Parse arguments.
	var parsedUrl = url.parse(req.url);
	var parsedQuery = querystring.parse(parsedUrl.query, '&', '=');
	
	// Need only one argument which is user identifier.
	if (Object.keys(parsedQuery).length != 1){
		res.write('Error: Too many or no argument.');
		res.end();
	}else{
		var user_id = parsedQuery.user;
		var name = req.body.name;
		var number = req.body.number;
		var code = req.body.code;
		
		var Contact = mongoose.model('Schema', new Schema({user : String, contact : [{name : String, number : String}]}, {strict : false}), 'contact_address');
		var condition1 = {"user" : user_id, "contact.name" : name};
		var condition2 = {"user" : user_id, "contact.number" : number};
		var update = {"$set" : {"contact.$.name" : name, "contact.$.number" : number}};
		var option = {upsert : true, new : true, useFindAndModify : false};
		
		Contact.find({"user" : user_id}, function(error, data){
			// Insert new data.
			if (code == '0'){
				newContact = {"name" : name, "number" : number};
				Contact.findOneAndUpdate({"user" : user_id}, {$push : {contact : newContact}}, option, function(error, change){
					if (error){
						console.log(error);
					}else{
						res.setHeader("Content-Type", "text/json");
						res.write(change.toString());
						res.end();
					}
				});
			// Modify name.
			}else if (code == '1'){
				console.log('Modify name.');
				// Find data using number because the name is changed.
				Contact.findOneAndUpdate(condition2, update, option, function(error, change){
					if (error){
						console.log(error);
					}else{
						res.setHeader("Content-Type", "text/json");
						res.write(change.toString());
						res.end();
					}
				});
			// Modify number.
			}else if (code == '2'){
				console.log('Modify number.');
				// Find data using name because the number is changed.
				Contact.findOneAndUpdate(condition1, update, option, function(error, change){
					if (error){
						console.log(error);
					}else{
						res.setHeader("Content-Type", "text/json");
						res.write(change.toString());
						res.end();
					}
				});
			// Delete.
			}else if (code == '3'){
				removeContact = {"name" : name, "number" : number};
				Contact.findOneAndUpdate(condition1, {$pull : {contact : removeContact}}, {new : true, useFindAndModify : false},  function(error, output){
					if (error){
						console.log(error);
					}else{
						res.write('Deleted.');
						res.end();
					}
				});
			// Error: Code not found.
			}else{
				console.log('Error: Code not found');
				res.write('Error: Code not found');
				res.end();
			}
			mongoose.deleteModel('Schema');
		});
	}
});

// For Tab2 - Get gallery.
// Require only one argument which is user identifier.
app.get('/getimage', function(req, res){
	// Parse arguments.
	var parsedUrl = url.parse(req.url);
	var parsedQuery = querystring.parse(parsedUrl.query, '&', '=');
	
	// Need only one argument which is user identifier.
	if (Object.keys(parsedQuery).length != 1){
		res.write('Error: Too many or no argument.');
		res.end();
	}else{
		var user_id = parsedQuery.user;
		var Image = mongoose.model('Schema', new Schema({user : String, image : [{name : String, content : String}]}), 'gallery');
		
		Image.find({"user" : user_id}, {"_id" : 0, "image" : 1}, function(error, data){
			console.log('Get Image.');
			if (error){
				console.log(error);
			}else{
				// If there's no entity for this user, make it.
				if (data.toString() == ""){
					Image.create({"user" : user_id, "image" : []}, function(error){
						console.log('Created new gallery.');
						// Set the response again.
						Image.find({"user" : user_id}, {"_id" : 0, "image" : 1}, function(error, newdata){
							if (error){
								console.log(error);
							}else{
								data = newdata;
								res.setHeader("Content-Type", "text/json");
								res.write(data.toString());
								res.end();
							}
						});
					});
				}else{
					res.setHeader("Content-Type", "text/json");
					res.write(data.toString());
					res.end();
				}	
			}
		});
		mongoose.deleteModel('Schema');
	}
});

// For Tab2 - Modify gallery.
// Require only one argument which is user identifier.
app.post('/postimage', function(req, res){
	console.log('Post image.');
	// Parse arguments.
	var parsedUrl = url.parse(req.url);
	var parsedQuery = querystring.parse(parsedUrl.query, '&', '=');
	
	// Need only one argument which is user identifier.
	if (Object.keys(parsedQuery).length != 1){
		res.write('Error: Too many or no argument.');
		res.end();
	}else{
		var user_id = parsedQuery.user;
		// Encoded content of image.
		var name = req.body.name;
		var content = req.body.content;
		var code = req.body.code;
		
		var Image = mongoose.model('Schema', new Schema({user : String, image : [{name : String, content : String}]}, {strict : false}), 'gallery');
		var condition = {"user" : user_id};
		var option = {upsert : true, new : true, useFindAndModify : false};
		
		// Insert new data.
		if (code == '0'){
			console.log('Insert image.');
			newImage = {"name" : name, "content" : content};
			//console.log(req);
			Image.findOneAndUpdate(condition, {$push : {image : newImage}}, option, function(error, change){
				if (error){
					console.log(error);
				}else{
					res.end(change.toString());
				}
			});
		// Delete.
		}else if (code == '1'){
			removeImage = {"name" : name, "content" : content};
			Image.findOneAndUpdate(condition, {$pull : {image : removeImage}}, option, function(error, output){
				if (error){
					console.log(error);
				}else{
					res.write('Deleted.');
					res.end();
				}
			});
		}else{
			console.log('Error: Code not found');
			res.write('Error: Code not found');
			res.end();
		}
		mongoose.deleteModel('Schema');
	}
});

// For Tab3 - Get todo list.
// Require only one argument which is user identifier.
app.get('/gettodo', function(req, res){
	// Parse arguments.
	var parsedUrl = url.parse(req.url);
	var parsedQuery = querystring.parse(parsedUrl.query, '&', '=');
	
	// Need only one argument which is user identifier.
	if (Object.keys(parsedQuery).length != 1){
		res.write('Error: Too many or no argument.');
		res.end();
	}else{
		var user_id = parsedQuery.user;
		var Todo = mongoose.model('Schema', new Schema({user : String, todo : [{number : String, content : String}]}), 'todolist');
		
		Todo.find({"user" : user_id}, {"_id" : 0, "todo" : 1}, function(error, data){
			console.log('Get todo list.');
			if (error){
				console.log(error);
			}else{
				// If there's no entity for this user, make it.
				if (data.toString() == ""){
					Todo.create({"user" : user_id, "todo" : []}, function(error){
						console.log('Create new todo list.');
						// Set the response again.
						Todo.find({"user" : user_id}, {"_id" : 0, "todo" : 1}, function(error, newdata){
							if (error){
								console.log(error);
							}else{
								data = newdata;
								res.setHeader("Content-Type", "text/json");
								res.write(data.toString());
								res.end();
							}
						});
					});
				}else{
					res.setHeader("Content-Type", "text/json");
					res.write(data.toString());
					res.end();
				}
			}
		});
		mongoose.deleteModel('Schema');
	}
});

// For Tab3 - Modify todo list.
// Require only one argument which is user identifier.
app.post('/posttodo', function(req, res){
	console.log('Post todo list.');
	// Parse arguments.
	var parsedUrl = url.parse(req.url);
	var parsedQuery = querystring.parse(parsedUrl.query, '&', '=');
	
	// Need only one argument which is user identifier.
	if (Object.keys(parsedQuery).length != 1){
		res.write('Error: Too many or no argument.');
		res.end();
	}else{
		var user_id = parsedQuery.user;
		var number = req.body.number;
		var content = req.body.content;
		var code = req.body.code;
		
		var Todo = mongoose.model('Schema', new Schema({user : String, todo : [{number : String, content : String}]}, {strict : false}), 'todolist');
		var option = {upsert : true, new : true, useFindAndModify : false}; 
		
		// Insert new data.
		if (code == '0'){
			console.log('Insert todo thing.');
			newTodo = {"number" : number, "content" : content};
			Todo.findOneAndUpdate({"user" : user_id}, {$push : {todo : newTodo}}, option, function(error, change){
				if (error){
					console.log(error);
				}else{
					res.end(change.toString());
				}
			});
		// Modify content.
		}else if (code == '1'){
			console.log('Modify todo thing.');
			newTodo = {"number" : number, "content" : content};
			Todo.findOneAndUpdate({"user" : user_id, "todo.number" : number}, {$set : {"todo.$.number" : number, "todo.$.content" : content}}, option, function(error, change){
				if (error){
					console.log(error);
				}else{
					res.setHeader("Content-Type", "text/json");
					res.write(change.toString());
					res.end();
				}
			});
		// Delete.
		}else if (code == '2'){
			removeTodo = {"number" : number, "content" : content};
			Todo.findOneAndUpdate({"user" : user_id}, {$pull : {todo : removeTodo}}, {new : true, useFindAndModify : false}, function(error, output){
				if (error){
					console.log(error);
				}else{
					res.write('Deleted.');
					res.end();
				}
			});
		}else{
			console.log('Error: Code not found');
			res.write('Error: Code not found');
			res.end();
		}
		mongoose.deleteModel('Schema');
	}
});
