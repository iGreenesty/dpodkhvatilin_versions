// Если заполнено имя контактного лица, то обращаемся по имени, иначе - "клиент"
notification.scriptParams['greeting'] = subject.title ? "Уважаемый(ая) ${subject.title}" : "Уважаемый клиент";

//Логин сотрудника
notification.scriptParams['login'] = subject.login

//Пароль сотрудника
def password = api.security.generatePassword();
notification.scriptParams['password'] = password;
utils.edit(subject, ['password' : password, 'isGenPass' : false]);

// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: '';