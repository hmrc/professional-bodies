
# professional-bodies

This is a placeholder README.md for a new repository

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

Start DataBase locally


E.G. - for deleting a organisation
curl \
    --header "Content-type: application/json" \
    --request DELETE \
    --data '{"name":"organisation"}' \
    http://localhost:7401/removeOrganisation

E.G. - for adding a organisation
curl \
    --header "Content-type: application/json" \
    --request POST \
    --data '{"name":"organisation"}' \
    http://localhost:7401/addOrganisation

E.G - for getting organisations
curl \
    --request GET \
    http://localhost:7401/organisations