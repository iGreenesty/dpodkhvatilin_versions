// Подпись к оповещению
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: '';

// Определение описания источника
notification.scriptParams['sourceDescr'] = subject.source?.description ?: subject.sourceR?.descriptionRTF ?: '[не указано]'