// Добавить согласующее лицо в получатели оповещения
notification.toEmployee << subject.voter_em

// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: ''                    