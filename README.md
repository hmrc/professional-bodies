
# professional-bodies

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

Start DataBase locally


E.G. - for deleting a Professional Body
curl \
    --header "Content-type: application/json" \
    --request DELETE \
    --data '{"name":"professionalBody", "id":"validID"}' \
    http://localhost:7401/professionalBodies

E.G. - for adding a Professional Body
curl \
    --header "Content-type: application/json" \
    --request POST \
    --data '{"name":"professionalBody"}' \
    http://localhost:7401/professionalBodies

E.G - for getting Professional Body
curl \
    --request GET \
    http://localhost:7401/professionalBodies