def author = sourceObject.author
def client = subject.clientEmployee
def clientOU = subject.clientOU
// Комментарий от контрагента, от сотрудника его отдела, от суперпользователя или служебного
return (author == client || author?.parent == clientOU || author == null || author?.title == "Служебный") ? "" : "Автор - не контрагент"