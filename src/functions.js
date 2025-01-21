function fetchVacancies(survey) {
    return $http.get("http://opendata.trudvsem.ru/api/v1/vacancies/region/" + survey.region + "?text=" + survey.job, {
        timeout: 10000
    });
}

function fetchRecommendations(sphere, area){
    var response = $http.get("https://2d11-185-177-229-241.ngrok-free.app/vacancy_recommendation?sphere=" + sphere + "&area=" + area, {
        timeout: 10000
    });
    var result = response.recommendations;
    return result;
}


// Функция для вывода вакансий с пагинацией
function showPage(page, vacancies) {
    var pageSize = 3;

    // Вычисляем индекс начала и конца текущей страницы
    var startIndex = page * pageSize;
    var endIndex = Math.min(startIndex + pageSize, vacancies.length);

    var message = "Вакансии с " + (startIndex + 1) + " по " + endIndex + " из " + vacancies.length + ":\n\n";

    // Формируем сообщение с вакансиями для текущей страницы
    vacancies.slice(startIndex, endIndex).forEach(function (vacancy) {
        var contact = (vacancy.vacancy.contact_list && vacancy.vacancy.contact_list.length > 0)
            ? vacancy.vacancy.contact_list[0].contact_value
            : "нет данных";

        message += "- **" + vacancy.vacancy.company.name + "** в *" + vacancy.vacancy.region.name + "*\n" +
                   "  Зарплата: " + (vacancy.vacancy.salary || "не указана") + "\n" +
                   "  Контакты: " + contact + "\n" +
                   "  Подробнее: " + vacancy.vacancy.vac_url + "\n\n";
    });

    $reactions.answer(message);

    var buttons = [];
    if (startIndex > 0) {
        buttons.push("предыдущая страница");
    }
    if (endIndex < vacancies.length) {
        buttons.push("следующая страница");
    }

    // Если есть кнопки, отображаем их
    if (buttons.length > 0) {
        $reactions.buttons(buttons);
    }
}