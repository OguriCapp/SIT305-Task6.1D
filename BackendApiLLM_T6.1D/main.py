import os
import requests
from flask import Flask, request, jsonify
import re

app = Flask(__name__)

# To setup API connection details
API_URL = "https://router.huggingface.co/novita/v3/openai/chat/completions"
HF_API_TOKEN = os.getenv('HF_API_TOKEN', '')  # To use env var or default
HEADERS = {"Authorization": "Bearer "}
# MODEL = "deepseek/deepseek-v3-0324"
# MODEL = "meta-llama/Llama-2-7b-chat-hf"
MODEL = "google/gemma-3-1b-it"

def fetchQuizFromLlama(student_topic):
    print("Fetching quiz from Hugging Face router API")
    payload = {
        "messages": [
            {
                "role": "user",
                "content": (
                    f"Generate a quiz with 3 questions to test students on the provided topic. "
                    f"For each question, generate 4 options where only one of the options is correct. "
                    f"Format your response as follows:\n"
                    f"**QUESTION 1:** [Your question here]?\n"
                    f"**OPTION A:** [First option]\n"
                    f"**OPTION B:** [Second option]\n"
                    f"**OPTION C:** [Third option]\n"
                    f"**OPTION D:** [Fourth option]\n"
                    f"**ANS:** [Correct answer letter]\n\n"
                    f"**QUESTION 2:** [Your question here]?\n"
                    f"**OPTION A:** [First option]\n"
                    f"**OPTION B:** [Second option]\n"
                    f"**OPTION C:** [Third option]\n"
                    f"**OPTION D:** [Fourth option]\n"
                    f"**ANS:** [Correct answer letter]\n\n"
                    f"**QUESTION 3:** [Your question here]?\n"
                    f"**OPTION A:** [First option]\n"
                    f"**OPTION B:** [Second option]\n"
                    f"**OPTION C:** [Third option]\n"
                    f"**OPTION D:** [Fourth option]\n"
                    f"**ANS:** [Correct answer letter]\n\n"
                    f"Do NOT include the placeholder text like '[Your question here]' or '[First option]'. "
                    f"Replace them with actual content related to the topic."
                    f"Ensure text is properly formatted. It needs to start with a question, then the options, and finally the correct answer. "
                    f"Follow this pattern for all questions. "
                    f"Here is the student topic:\n{student_topic}"
                )
            }
        ],
        "model": MODEL,
        "max_tokens": 500,
        "temperature": 0.7,
        "top_p": 0.9
    }

    # To send request to AI and get response
    response = requests.post(API_URL, headers=HEADERS, json=payload)
    if response.status_code == 200:
        result = response.json()["choices"][0]["message"]["content"]
        # print(result)
        return result
    else:
        raise Exception(f"API request failed: {response.status_code} - {response.text}")

def process_quiz(quiz_text):
    questions = []
    # To find all questions in AI response
    pattern = re.compile(
        r'\*\*QUESTION \d+:\*\* (.+?)\n'
        r'\*\*OPTION A:\*\* (.+?)\n'
        r'\*\*OPTION B:\*\* (.+?)\n'
        r'\*\*OPTION C:\*\* (.+?)\n'
        r'\*\*OPTION D:\*\* (.+?)\n'
        r'\*\*ANS:\*\* (.+?)(?=\n\n|\Z)',
        re.DOTALL
    )
    matches = pattern.findall(quiz_text)
    print(f"Found {len(matches)} questions in the quiz text")

    # To create nice question objects from matches
    for match in matches:
        question = match[0].strip()
        options = [match[1].strip(), match[2].strip(), match[3].strip(), match[4].strip()]
        correct_ans = match[5].strip()

        # To check if question still has template text
        if "[Your question here]" in question or "[First option]" in options[0]:
            continue  # To skip template questions

        # To create question data structure
        question_data = {
            "question": question,
            "options": options,
            "correct_answer": correct_ans
        }
        questions.append(question_data)

    return questions

@app.route('/getQuiz', methods=['GET'])
def get_quiz():
    print("Request received")
    # To get topic from request
    student_topic = request.args.get('topic')
    if not student_topic:
        return jsonify({'error': 'Missing topic parameter'}), 400
    try:
        # To get quiz from AI
        quiz = fetchQuizFromLlama(student_topic)
        print(quiz)
        # To convert AI text to structured data
        processed_quiz = process_quiz(quiz)
        if not processed_quiz:
            return jsonify({'error': 'Failed to parse quiz data', 'raw_response': quiz}), 500
        # To send quiz to app
        return jsonify({'quiz': processed_quiz}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/test', methods=['GET'])
def run_test():
    # To check if server is running
    return jsonify({'quiz': "test"}), 200

if __name__ == '__main__':
    # To start web server
    port_num = 5000
    print(f"App running on port {port_num}")
    app.run(port=port_num, host="0.0.0.0")
