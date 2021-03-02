// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: '';

//Описание заявки
notification.scriptParams['description'] = subject.descriptionRTF ?: '[не указано]'