// Если заполнено имя контактного лица, то обращаемся по имени, иначе - "клиент"
notification.scriptParams['greeting'] = subject.clientName ? "Уважаемый(ая) ${subject.clientName}" : "Уважаемый клиент"

// Получение текста последнего комментария
def lastComment = sourceObject
notification.scriptParams['commentText'] = lastComment.text;

// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: '';

//Добавляет файлы из комментария как вложение к письму
def files = lastComment.files
notification.attachments.addAll(files)

//Добавляет контактный e-mail в список получателей оповещения.
def CLIENT_EMAIL = 'clientEmail' // Код атрибута "Контактный е-мэйл"
def CLIENT_NAME = 'clientName' // Код атрибута "Контактное лицо"
def clientEmail = subject[CLIENT_EMAIL]
notification.to[clientEmail] = subject[CLIENT_NAME]

// Исключение автора комментария из получателей
def author = lastComment.author
if(author != null) {
  notification.toEmployee.remove(author)
  notification.to.remove(author.email)
}