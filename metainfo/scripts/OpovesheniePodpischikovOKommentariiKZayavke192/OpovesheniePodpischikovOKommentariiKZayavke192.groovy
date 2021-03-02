// Получение даты и текста последнего комментария
def lastComment = sourceObject
notification.scriptParams['commentText'] = lastComment.text;

// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: '';

//Добавляет файлы из комментария как вложение к письму
def files = lastComment.files
notification.attachments.addAll(files)

// Исключение автора комментария из получателей
def author = lastComment.author
if(author != null) {
  notification.toEmployee.remove(author)
  notification.to.remove(author.email)
}