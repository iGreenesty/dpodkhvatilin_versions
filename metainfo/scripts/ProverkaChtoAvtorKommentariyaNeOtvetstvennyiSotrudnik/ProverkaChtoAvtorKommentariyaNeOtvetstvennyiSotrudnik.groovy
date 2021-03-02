def author = sourceObject.author
def responsible = subject.responsibleEmployee
//проверка, что автор комментария - не ответственный сотрудник
return (responsible != null && author?.UUID == responsible?.UUID) ? "Автор - ответственный сотрудник" : ""