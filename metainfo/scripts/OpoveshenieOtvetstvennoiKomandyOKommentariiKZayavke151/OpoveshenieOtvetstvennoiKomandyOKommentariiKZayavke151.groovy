// Получение даты и текста последнего комментария
def lastComment = sourceObject
notification.scriptParams['commentText'] = lastComment.text;
notification.scriptParams['author'] = lastComment.author?.title ?: 'Суперпользователь'

def client = "";
if(subject.clientOU == null && subject.clientEmployee == null) {
  client = "[не указан]"
}
else {
  client = (subject.clientEmployee) ? "${subject.clientEmployee.title} / " : "";
  client = client + subject.clientOU?.title
}
notification.scriptParams['client'] = client

//Добавляет файлы из комментария как вложение к письму
def files = lastComment.files
notification.attachments.addAll(files)

// Исключение автора комментария из получателей
def author = lastComment.author
if(author != null) {
  notification.toEmployee.remove(author)
  notification.to.remove(author.email)
}

// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: ''                  