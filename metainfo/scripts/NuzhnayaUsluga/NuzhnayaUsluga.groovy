def services = ["RM"]

if(services.any{ subject.service.inventoryNumber }) return "" else return 'nope'