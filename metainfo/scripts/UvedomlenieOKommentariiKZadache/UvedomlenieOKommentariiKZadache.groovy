def lastComment = sourceObject

//Текст комментария
notification.scriptParams['commentText'] = lastComment.text;

// Подпись к оповещению по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: '';

//Добавляет файлы из комментария как вложение к письму
def files = lastComment.files
notification.attachments.addAll(files)

// Список получателей определяется уведомляемыми в комментарии
notification.toEmployee += lastComment.notificateTo