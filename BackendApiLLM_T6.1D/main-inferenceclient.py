import os
from flask import Flask, request, jsonify
import re
from huggingface_hub import InferenceClient

app = Flask(__name__)

# To setup connection to AI service
HF_API_TOKEN = os.getenv('HF_API_TOKEN', '')  # To get API key from computer
client = InferenceClient(
    provider="novita",
    api_key=HF_API_TOKEN,
)
# MODEL = "deepseek/deepseek-v3-0324"
# MODEL = "meta-llama/Llama-2-7b-chat-hf"
# MODEL = "deepseek-ai/DeepSeek-R1"
MODEL = "google/gemma-3-1b-it"

def fetchQuizFromLlama(student_topic):
    print("Fetching quiz from Hugging Face InferenceClient")
    # To create instruction for AI
    prompt = (
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
        f"Ensure text is properly formatted. It needs to start with a question, then the options, and finally the correct answer. "
        f"Follow this pattern for all questions. "
        f"Here is the student topic:\n{student_topic}"
    )

    try:
        # To send request to AI service
        completion = client.chat.completions.create(
            model=MODEL,
            messages=[
                {
                    "role": "user",
                    "content": prompt
                }
            ],
            max_tokens=500,
            temperature=0.7,
            top_p=0.9
        )
        # To get the text from response
        result = completion.choices[0].message.content  # To get answer from response object
        return result
    except Exception as e:
        raise Exception(f"Failed to fetch quiz from InferenceClient: {str(e)}")

def process_quiz(quiz_text):
    questions = []
    # To find all questions in AI response text
    pattern = re.compile(
        r'\*\*QUESTION \d+:\*\* (.+?)\n'
        r'\*\*OPTION A:\*\* (.+?)\n'
        r'\*\*OPTION B:\*\* (.+?)\n'
        r'\*\*OPTION C:\*\* (.+?)\n'
        r'\*\*OPTION D:\*\* (.+?)\n'
        r'\*\*ANS:\*\* (.+?)(?=\n|$)',
        re.DOTALL
    )
    matches = pattern.findall(quiz_text)

    # To create question objects from text
    for match in matches:
        question = match[0].strip()
        options = [match[1].strip(), match[2].strip(), match[3].strip(), match[4].strip()]
        correct_ans = match[5].strip()

        # To make data structure for each question
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
    # To get topic from app request
    student_topic = request.args.get('topic')
    if not student_topic:
        return jsonify({'error': 'Missing topic parameter'}), 400
    try:
        # To get quiz from AI
        quiz = fetchQuizFromLlama(student_topic)
        print(quiz)
        # To convert AI text to quiz format
        processed_quiz = process_quiz(quiz)
        if not processed_quiz:
            return jsonify({'error': 'Failed to parse quiz data', 'raw_response': quiz}), 500
        # To send quiz back to app
        return jsonify({'quiz': processed_quiz}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/test', methods=['GET'])
def run_test():
    # To check if server is working
    return jsonify({'quiz': "test"}), 200

if __name__ == '__main__':
    # To start web server
    port_num = 5000
    print(f"App running on port {port_num}")
    app.run(port=port_num, host="0.0.0.0")
