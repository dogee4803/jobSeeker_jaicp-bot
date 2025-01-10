function fetchVacancies() {
    return $http.get("http://opendata.trudvsem.ru/api/v1/vacancies/region/03?text=программист", {
        timeout: 10000
    });
}

function printVacancies(vacancies) {
    vacancies.forEach(function (vacancy) {
        var message = "- " + vacancy.vacancy.company.name + " в " + vacancy.vacancy.region.name + "\n" +
                      "  Зарплата: " + vacancy.vacancy.salary + "\n" +
                      "  Контакты: " + vacancy.vacancy.contact_list[0].contact_value + "\n" +
                      "  [Подробнее](" + vacancy.vacancy.vac_url + ")\n";
        $reactions.answer(message);
    });
}

