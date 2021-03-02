def ATTRS_TO_CHECK = ['deadline', 'planDate', 'state']
return (ATTRS_TO_CHECK.any {oldSubject[(it)] != subject[(it)]} ) ? '' : 'Статус, дедлайн или плановая дата начала не изменились'