const url = 'http://localhost:8080/api/admin';
const userUrl = 'http://localhost:8080/api/current_user';

document.addEventListener("DOMContentLoaded", async function () {
    const authUser = await getAuthUser();
    console.log(authUser);
    await fillHeaderText(authUser);
})

function getAllUsers() {
    fetch(url)
        .then(res => res.json())
        .then(data => {
            loadTable(data)
        })
}

function getAdminPage() {
    fetch(url).then(response => response.json()).then(user => {
        console.log(user);
        loadTable(user)
    })
}

function loadTable(listAllUsers) {
    let res = '';
    for (let user of listAllUsers) {
        res +=
            `<tr>
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.lastName}</td>
                <td>${user.username}</td>
                <td id=${'role' + user.id}>${user.roles.map(r => r.name).join(' ')}</td>
                <td>
                    <button class="btn btn-info" type="button"
                    data-bs-toggle="modal" data-bs-target="#editModal"
                    onclick="editModal(${user.id})">Edit</button></td>
                <td>
                    <button class="btn btn-danger" type="button"
                    data-bs-toggle="modal" data-bs-target="#deleteModal"
                    onclick="deleteModal(${user.id})">Delete</button></td>
            </tr>`
    }
    document.getElementById('tableBodyAdmin').innerHTML = res;
}

function getInformationAboutUser(user) {
    console.log(user);

    let result = '';
    result =
        `<tr>
    <td>${user.id}</td>
    <td>${user.name}</td>
    <td>${user.lastName}</td>
    <td>${user.username}</td>
    <td id=${'role' + user.id}>${user.roles.map(r => r.name).join(' ')}</td>
</tr>`
    document.getElementById('userTableBody').innerHTML = result;
}

function getUserPage() {
    fetch(userUrl).then(response => response.json()).then(user =>
        getInformationAboutUser(user))
}


getAdminPage();
getUserPage();


// Добавление пользователя
document.getElementById('newUserForm').addEventListener('submit', (e) => {
    e.preventDefault()
    let role = document.getElementById('role_select')
    let rolesAddUser = []
    let rolesAddUserValue = ''
    for (let i = 0; i < role.options.length; i++) {
        if (role.options[i].selected) {
            rolesAddUser.push({id: role.options[i].value, name: 'ROLE_' + role.options[i].innerHTML})
            rolesAddUserValue += role.options[i].innerHTML
        }
    }
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify({
            name: document.getElementById('newName').value,
            lastName: document.getElementById('newLastName').value,
            username: document.getElementById('newUsername').value,
            password: document.getElementById('newPassword').value,
            roles: rolesAddUser
        })
    })
        .then((response) => {
            if (response.ok) {
                getAllUsers()
                document.getElementById("all-users-tab").click()
            }
        })
})


// Закрытие модального окна
function closeModal() {
    // document.getElementById("editClose").click()
    document.querySelectorAll(".btn-close").forEach((btn) => btn.click())
}


//Редактирование пользователя
function editModal(id) {
    fetch(url + '/' + id, {
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json;charset=UTF-8'
        }
    }).then(res => {
        res.json().then(u => {

            document.getElementById('editId').value = u.id;
            document.getElementById('editName').value = u.name;
            document.getElementById('editLastName').value = u.lastName;
            document.getElementById('editUsername').value = u.username;
            document.getElementById('editPassword').value = "****";

        })
    });
}


async function editUser() {
    const form_ed = document.getElementById('modalEdit');
    let idValue = document.getElementById("editId").value;
    let nameValue = document.getElementById("editName").value;
    let lastNameValue = document.getElementById("editLastName").value;
    let username = document.getElementById("editUsername").value;
    let passwordValue = document.getElementById("editPassword").value;
    let listOfRole = [];
    for (let i = 0; i < form_ed.roles.options.length; i++) {
        if (form_ed.roles.options[i].selected) {
            let tmp = {};
            tmp["id"] = form_ed.roles.options[i].value
            listOfRole.push(tmp);
            console.log(listOfRole);
        }
    }
    let user = {
        id: idValue,
        name: nameValue,
        lastName: lastNameValue,
        username: username,
        password: passwordValue,
        roles: listOfRole
    }
    await fetch(url + '/' + user.id, {
        method: "PATCH",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json;charset=UTF-8'
        },
        body: JSON.stringify(user)
    });
    closeModal()
    getAllUsers()
}


// Удаление пользователя
function deleteModal(id) {
    fetch(url + '/' + id, {
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json;charset=UTF-8'
        }
    }).then(res => {
        res.json().then(u => {
            console.log(u);
            document.getElementById('deleteId').value = u.id;
            document.getElementById('deleteName').value = u.name;
            document.getElementById('deleteLastName').value = u.lastName;
            document.getElementById('deleteUsername').value = u.username;
            document.getElementById("deleteRole").value = u.roles.map(r => r.name).join(", ");
        })
    });
}

async function deleteUser() {
    const id = document.getElementById("deleteId").value
    console.log(id)
    let urlDel = url + "/" + id;
    let method = {
        method: 'DELETE',
        headers: {
            "Content-Type": "application/json"
        }
    }

    fetch(urlDel, method).then(() => {
        closeModal()
        getAllUsers()
    })

}

async function fillHeaderText(authUser) {
    document.getElementById("header_text").innerText =
        `${authUser['username']} with roles: ${getUserRole(authUser['roles'])}`
}

function getUserRole(roles) {
    let result = "";
    for (const role of roles) {
        result += role["name"].replace('ROLE_', '') + " ";
    }
    console.log(result);
    return result;
}

async function getAuthUser() {
    const response = await fetch(`http://localhost:8080/api/current_user`);
    const authUser = await response.json();
    return authUser;
}
